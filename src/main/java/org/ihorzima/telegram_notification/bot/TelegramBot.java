package org.ihorzima.telegram_notification.bot;

import lombok.extern.slf4j.Slf4j;
import org.ihorzima.telegram_notification.model.Account;
import org.ihorzima.telegram_notification.repository.AccountLocalRepository;
import org.ihorzima.telegram_notification.util.InlineAccountCache;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class TelegramBot extends TelegramLongPollingBot {
    private static final String ENTER_SEARCH_BUTTON_ID = "btn2";
    private static final String ENTER_MANUAL_BUTTON_ID = "btn1";
    private static final Pattern RECEIVED_LAND_ID_PATTERN = Pattern.compile("(?<=ділянку:\\s?).*");
    private static final String ADMIN_CHAT_ID = "578803967";

    private static final int INLINE_QUERY_LIMIT = 50;
    private final AccountLocalRepository accountRepository;

    public TelegramBot(String botToken, AccountLocalRepository accountRepository) {
        super(botToken);
        this.accountRepository = accountRepository;
    }

    @Override
    public String getBotUsername() {
        // TODO: move to application properties or env
        return "@NotificationSender001bot";
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

//            if(update.getInlineQuery() != null) {
//                Account account = InlineAccountCache.retrieve(update.getInlineQuery().getId());
//                if (account != null) {
//                    InputTextMessageContent messageContent = new InputTextMessageContent(
//                            "ℹ *Інформація про ділянку:*\n" +
//                                    "🏷 *ID:* `" + escapeMarkdownV2(account.getLandId()) + "`\n" +
//                                    "📍 *Адреса:* " + escapeMarkdownV2(account.getAddress()) + "\n" +
//                                    "📞 *Телефон:* " + escapeMarkdownV2(account.getPhoneNumber() != null ? account.getPhoneNumber() : "Не указан"));
//                    messageContent.setParseMode("MarkdownV2");
//                    SendMessage message = new SendMessage();
//                    message.setText(messageContent.toString());
//                    message.setChatId("578803967");
//                    message.setText("Оберіть варіант");
//                    execute(message);
//                }
//            }

            if (update.hasMessage() && update.getMessage().hasText()) {
                handleMessage(update.getMessage());
            }
        } catch (TelegramApiException e) {
            log.error("Couldn't process telegram message: [{}]", update.getUpdateId(), e);
        }

    }

    private void handleMessage(Message message) throws TelegramApiException {
        String chatId = message.getChatId().toString();
        String receivedText = message.getText();

        if (receivedText.equals("/start")) {
            createButton(chatId);

        } else {
            Matcher matcher = RECEIVED_LAND_ID_PATTERN.matcher(receivedText);

            if (matcher.find()) {
                log.info("Found received land ID: {}", matcher.group());
                String landId = matcher.group().trim();
                SendMessage messageForAdmin = new SendMessage();
                messageForAdmin.setChatId(ADMIN_CHAT_ID);
                messageForAdmin.setText("*Ділянка:* `" + chatId + "`\n*ChatId:* `" + landId + "`");

                messageForAdmin.setParseMode(ParseMode.MARKDOWNV2);

                execute(messageForAdmin);
            }
        }
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
        InlineKeyboardButton button1 = new InlineKeyboardButton("Ввести номер ділянки вручну");
        button1.setCallbackData(ENTER_MANUAL_BUTTON_ID);

        InlineKeyboardButton button2 = new InlineKeyboardButton("Вибрати номер ділянки зі списку");
//        button2.setCallbackData(ENTER_SEARCH_BUTTON_ID);
        button2.setSwitchInlineQueryCurrentChat("");// можно пустую строку ""
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(button1);
        row.add(button2);

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Оберіть варіант");
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Couldn't send message", e);
        }
    }

    private void createMarkUp(String chatId) {
        InlineKeyboardButton button = new InlineKeyboardButton("🔍 Начать поиск");
        button.setText("Search");
        button.setSwitchInlineQueryCurrentChat("");// можно пустую строку ""

        List<InlineKeyboardButton> row = List.of(button);
        List<List<InlineKeyboardButton>> keyboard = List.of(row);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Нажми на кнопку, чтобы начать Inline-запрос:");
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
        }
    }


    private void handleInlineQuery(InlineQuery inlineQuery) throws TelegramApiException {
        String query = inlineQuery.getQuery().toLowerCase();
        String offset = inlineQuery.getOffset(); // Получаем `offset` от Telegram

        // Определяем с какого элемента начинать
        int offsetIndex = offset.isEmpty() ? 0 : Integer.parseInt(offset);

        // Получаем все аккаунты, соответствующие запросу
        List<Account> accounts = accountRepository.getAccounts().stream()
                .filter(account -> account.getLandId().toLowerCase().contains(query))
                .toList();

        if (accounts.isEmpty()) {
            log.debug("Accounts for query [{}] not found", query);
            return;
        }

        log.info("Found {} accounts for query: {}", accounts.size(), query);

        // Реализуем пагинацию (берём нужную порцию данных)
        List<Account> paginatedAccounts = accounts.stream()
                .skip(offsetIndex) // Пропускаем записи по `offset`
                .limit(INLINE_QUERY_LIMIT) // Берём максимум 50 записей
                .toList();

        List<InlineQueryResult> accountResults = convertAccountToInlineQueryResults(paginatedAccounts);

        // Определяем `next_offset` (если есть ещё данные, передаём Telegram)
        String nextOffset = (offsetIndex + INLINE_QUERY_LIMIT < accounts.size())
                ? String.valueOf(offsetIndex + INLINE_QUERY_LIMIT)
                : ""; // Если данных больше нет, `next_offset` пустой

        AnswerInlineQuery answer = buildAnswerInlineQuery(inlineQuery, accountResults, nextOffset);
        execute(answer);
    }

    private List<InlineQueryResult> convertAccountToInlineQueryResults(List<Account> accounts) {
        // casting to InlineQueryResult
        return accounts.stream().map(this::buildAccountInlineQueryResult)
                .map(article -> (InlineQueryResult) article)  // casting to InlineQueryResult
                .toList();
    }

    private AnswerInlineQuery buildAnswerInlineQuery(InlineQuery inlineQuery, List<InlineQueryResult> queryResults, String nextOffset) {
        AnswerInlineQuery answer = new AnswerInlineQuery();
        answer.setInlineQueryId(inlineQuery.getId());
        answer.setResults(queryResults);
        // TODO: extract to property. Currently no caching
        answer.setCacheTime(1); // Кешируем 5 секунд
        answer.setIsPersonal(true); // Результаты видит только пользователь
        answer.setNextOffset(nextOffset); // Передаём `next_offset`

        return answer;
    }

    private InlineQueryResultArticle buildAccountInlineQueryResult(Account account) {
        InputTextMessageContent messageContent = new InputTextMessageContent(
                "✅ *Ви вибрали ділянку:* `" + escapeMarkdownV2(account.getLandId()) + "`"
        );
        messageContent.setParseMode("MarkdownV2");

        return InlineQueryResultArticle.builder()
                .id(InlineAccountCache.store(account) + "_" + System.nanoTime()) // ✅ Делаем ID уникальным
                .title("🌍 Земельна ділянка: " + account.getLandId())
                .description(account.getAddress())
                .inputMessageContent(messageContent)
                .build();
    }


    String escapeMarkdownV2(String text) {
        if (text == null) return ""; // Если null — возвращаем пустую строку
        return text.replaceAll("([_*.\\[\\]()~`>#+\\-=|{}!])", "\\\\$1");
    }

    private void sendTextMessage(String chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        execute(message);
    }
}
