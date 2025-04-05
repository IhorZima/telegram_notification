package org.ihorzima.telegram_notification.bot;

import lombok.extern.slf4j.Slf4j;
import org.ihorzima.telegram_notification.model.Account;
import org.ihorzima.telegram_notification.repository.AccountRepository;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final AccountRepository accountRepository;

    public TelegramBot(String botToken, AccountRepository accountRepository) {
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

            if (update.hasMessage() && update.getMessage().hasText()) {
                String chatId = update.getMessage().getChatId().toString();
                String receivedText = update.getMessage().getText();

                if (receivedText.equals("/start")) {
                    sendTextMessage(chatId, "hello it's your Telegram bot 🚀");
                }
            }
        } catch (TelegramApiException e) {
            log.error("Couldn't process telegram message: [{}]", update.getUpdateId(), e);
        }

    }

    public void handleInlineQuery(InlineQuery inlineQuery) throws TelegramApiException {
        String query = inlineQuery.getQuery().toLowerCase();

        List<Account> accounts = accountRepository.findAllByLandIdStartingWith(query);

        if (accounts.isEmpty()) {
            log.debug("Accounts for query: [{}] not found", inlineQuery);
            return;
        }
        log.info("Found {} accounts", accounts.size());
        List<InlineQueryResult> accountListInlineQueryResult = convertAccountToInlineQueryResults(accounts);
        AnswerInlineQuery answer = buildAnswerInlineQuery(inlineQuery, accountListInlineQueryResult);

        execute(answer);
    }

    private List<InlineQueryResult> convertAccountToInlineQueryResults(List<Account> accounts) {
        // casting to InlineQueryResult
        return accounts.stream().map(this::buildAccountInlineQueryResult)
                .map(article -> (InlineQueryResult) article)  // casting to InlineQueryResult
                .toList();
    }

    private AnswerInlineQuery buildAnswerInlineQuery(InlineQuery inlineQuery, List<InlineQueryResult> queryResults) {
        AnswerInlineQuery answer = new AnswerInlineQuery();
        answer.setInlineQueryId(inlineQuery.getId());
        answer.setResults(queryResults);
        // TODO: extract to property. Currently no caching
        answer.setCacheTime(1);
        return answer;
    }

    private InlineQueryResultArticle buildAccountInlineQueryResult(Account account) {
        return InlineQueryResultArticle.builder()
                .id(account.getLandId())
                .title(account.getLandId())
                .description(account.getAddress())
                .inputMessageContent(new InputTextMessageContent(account.getLandId()))
                .build();
    }

    private void sendTextMessage(String chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        execute(message);
    }
}
