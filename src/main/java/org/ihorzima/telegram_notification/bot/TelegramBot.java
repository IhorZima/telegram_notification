package org.ihorzima.telegram_notification.bot;

import lombok.extern.slf4j.Slf4j;
import org.ihorzima.telegram_notification.config.TelegramBotProperties;
import org.ihorzima.telegram_notification.model.Account;
import org.ihorzima.telegram_notification.repository.AccountLocalRepository;
import org.ihorzima.telegram_notification.util.InlineAccountCache;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@EnableConfigurationProperties(TelegramBotProperties.class)
public class TelegramBot extends TelegramLongPollingBot {
    private static final String ENTER_MANUAL_BUTTON_ID = "btn1";
    private static final Pattern RECEIVED_LAND_ID_PATTERN = Pattern.compile("(?<=ділянку:\\s?).*");
    private static final int INLINE_QUERY_LIMIT = 50;

    private final AccountLocalRepository accountRepository;
    private final TelegramBotProperties telegramBotProperties;

    public TelegramBot(String botToken, AccountLocalRepository accountRepository, TelegramBotProperties telegramBotProperties) {
        super(botToken);
        this.accountRepository = accountRepository;
        this.telegramBotProperties = telegramBotProperties;
    }

    @Override
    public String getBotUsername() {
        return telegramBotProperties.getUserName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasInlineQuery()) {
                handleInlineQuery(update.getInlineQuery());
            }
            if (update.hasCallbackQuery()) {
                CallbackQuery callbackQuery = update.getCallbackQuery();
                Long chatId = callbackQuery.getFrom().getId();
                String data = callbackQuery.getData();
                if (ENTER_MANUAL_BUTTON_ID.equals(data)) {
                    sendTextMessage(chatId.toString(), "будь ласка введіть номер ділянки");
                }
            }

            if (update.hasMessage() && update.getMessage().hasText()) {
                handleMessage(update.getMessage());
            }
        } catch (TelegramApiException e) {
            log.error("Couldn't process telegram message: [{}]", update.getUpdateId(), e);
        }
    }

    private void handleMessage(Message message) throws TelegramApiException {
        String originalChatId = message.getChatId().toString();
        String receivedText = message.getText();

        if (receivedText.equals("/start")) {
            createButton(originalChatId);
        } else if (receivedText.equals("/admin " + telegramBotProperties.getAdminPassphrase())) {
            handleAdminRegisterRequest(originalChatId);
        } else {
            handleSelectedLandRequest(receivedText, originalChatId);
        }
    }

    private void handleSelectedLandRequest(String receivedText, String originalChatId) throws TelegramApiException {
        Set<String> adminChatIds = telegramBotProperties.getAdminChatIds();

        if (adminChatIds.isEmpty()) {
            log.warn("No admin chatIds found for chatId: {}", originalChatId);
            return;
        }

        Matcher matcher = RECEIVED_LAND_ID_PATTERN.matcher(receivedText);

        if (matcher.find()) {
            String landId = matcher.group().trim();
            log.info("Found received landId: {}", landId);

            for (String adminChatId : adminChatIds) {
                String text = "*Ділянка:* `" + landId + "`\n*chatId:* `" + originalChatId + "`";
                SendMessage messageToAdmin = buildTextMessageForChatId(adminChatId, text);
                messageToAdmin.setParseMode(ParseMode.MARKDOWNV2);
                execute(messageToAdmin);
            }
        } else {
            for (String adminChatId : adminChatIds) {
                String text = "*Ділянка:* `" + receivedText + "`\n*chatId:* `" + originalChatId + "`";
                SendMessage messageToAdmin = buildTextMessageForChatId(adminChatId, text);
                messageToAdmin.setParseMode(ParseMode.MARKDOWNV2);
                execute(messageToAdmin);
            }
        }
    }

    private void handleAdminRegisterRequest(String originalChatId) throws TelegramApiException {
        Set<String> adminChatIds = telegramBotProperties.getAdminChatIds();

        String adminResponseMessage;
        if (adminChatIds.contains(originalChatId)) {
            log.info("Admin with chatId: {} is already registered", originalChatId);
            adminResponseMessage = "Користувач вже зареєстрований як адмін. Твій chatId: " + originalChatId;
        } else {
            adminChatIds.add(originalChatId);
            log.info("User with chatId: {} successfully registered", originalChatId);
            adminResponseMessage = "Користувач успішно зареєстрований як адмін. Твій chatId: " + originalChatId;
        }
        execute(buildTextMessageForChatId(originalChatId, adminResponseMessage));
    }

    private SendMessage buildTextMessageForChatId(String adminChatId, String message) {
        SendMessage messageForAdmin = new SendMessage();
        messageForAdmin.setChatId(adminChatId);
        messageForAdmin.setText(message);
        return messageForAdmin;
    }

    public void sendFile(String chatId, String fileName, byte[] fileContent) throws TelegramApiException {
        InputStream pdfStream = new ByteArrayInputStream(fileContent);

        InputFile inputFile = new InputFile(pdfStream, fileName);

        SendDocument documentFile = new SendDocument();
        documentFile.setChatId(chatId);
        documentFile.setDocument(inputFile);
        documentFile.setCaption(fileName);

        execute(documentFile);
    }


    private void createButton(String chatId) {
        List<List<InlineKeyboardButton>> rows = getLists();

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Оберіть варіант для вводу номера ділянки");
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Couldn't send message", e);
        }
    }

    private List<List<InlineKeyboardButton>> getLists() {
        InlineKeyboardButton button1 = new InlineKeyboardButton("Ввести вручну");
        button1.setCallbackData(ENTER_MANUAL_BUTTON_ID);
        InlineKeyboardButton button2 = new InlineKeyboardButton("🔍 Вибрати номер зі списку");
        button2.setSwitchInlineQueryCurrentChat("");
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(button1);
        row.add(button2);

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row);
        return rows;
    }

    private void handleInlineQuery(InlineQuery inlineQuery) throws TelegramApiException {
        String query = inlineQuery.getQuery().toLowerCase();
        String offset = inlineQuery.getOffset();

        int offsetIndex = offset.isEmpty() ? 0 : Integer.parseInt(offset);

        List<Account> accounts = accountRepository.getAccounts().stream()
                .filter(account -> account.getLandId().toLowerCase().contains(query))
                .toList();

        if (accounts.isEmpty()) {
            log.debug("Accounts for query [{}] not found", query);
            return;
        }

        log.info("Found {} accounts for query: {}", accounts.size(), query);

        List<Account> paginatedAccounts = accounts.stream()
                .skip(offsetIndex)
                .limit(INLINE_QUERY_LIMIT)
                .toList();

        List<InlineQueryResult> accountResults = convertAccountToInlineQueryResults(paginatedAccounts);

        String nextOffset = (offsetIndex + INLINE_QUERY_LIMIT < accounts.size())
                ? String.valueOf(offsetIndex + INLINE_QUERY_LIMIT)
                : "";

        AnswerInlineQuery answer = buildAnswerInlineQuery(inlineQuery, accountResults, nextOffset);
        execute(answer);
    }

    private List<InlineQueryResult> convertAccountToInlineQueryResults(List<Account> accounts) {
        return accounts.stream().map(this::buildAccountInlineQueryResult)
                .map(article -> (InlineQueryResult) article)
                .toList();
    }

    private AnswerInlineQuery buildAnswerInlineQuery(InlineQuery inlineQuery, List<InlineQueryResult> queryResults, String nextOffset) {
        AnswerInlineQuery answer = new AnswerInlineQuery();
        answer.setInlineQueryId(inlineQuery.getId());
        answer.setResults(queryResults);
        answer.setCacheTime(5);
        answer.setIsPersonal(true);
        answer.setNextOffset(nextOffset);
        return answer;
    }

    private InlineQueryResultArticle buildAccountInlineQueryResult(Account account) {
        InputTextMessageContent messageContent = new InputTextMessageContent(
                "✅ *Ви вибрали ділянку:* `" + escapeMarkdownV2(account.getLandId()) + "`"
        );
        messageContent.setParseMode("MarkdownV2");

        return InlineQueryResultArticle.builder()
                .id(InlineAccountCache.store(account) + "_" + System.nanoTime())
                .title("🌍 Земельна ділянка: " + account.getLandId())
                .description(account.getAddress())
                .inputMessageContent(messageContent)
                .build();
    }


    private String escapeMarkdownV2(String text) {
        if (text == null) return "";
        return text.replaceAll("([_*.\\[\\]()~`>#+\\-=|{}!])", "\\\\$1");
    }

    private void sendTextMessage(String chatId, String text) throws TelegramApiException {
        execute(buildTextMessageForChatId(chatId, text));
    }
}
