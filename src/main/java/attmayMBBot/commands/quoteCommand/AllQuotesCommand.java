package attmayMBBot.commands.quoteCommand;

import attmayMBBot.commands.ICommand;
import attmayMBBot.config.AttmayMBBotConfig;
import attmayMBBot.functionalities.quoteManagement.Quote;
import attmayMBBot.functionalities.quoteManagement.QuoteManager;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class AllQuotesCommand implements ICommand {
    private AttmayMBBotConfig config;
    private QuoteManager quoteManager;
    public AllQuotesCommand(AttmayMBBotConfig config, QuoteManager quoteManager){
        this.config = config;
        this.quoteManager = quoteManager;
    }

    @Override
    public void execute(Message message, String[] args) {
        List<Pair<String,Quote>> quoteList = null;
        if(args.length == 1){
            //Default case: print all the quotes
            quoteList = quoteManager.getAllQuotesSortedByIssuedDate();
        } else if (args.length > 1){
            //special case: print quotes by a specific author
            String authorName = args[2];
            if(quoteManager.checkIfQuoteAuthorNameExists(authorName)){
                quoteList = quoteManager.getAllQuotesFromAuthorSortedByIssuedDate(authorName);
            } else {
                message.getChannel().block().createMessage("Author not found!").block();
                return;
            }
        }
        if(quoteList != null) {
            //quoteList has the proper quotes to print here!
            List<String> embedDescriptions = new ArrayList<>();
            String embedTitle = "List of all Quotes";
            StringBuilder sb = new StringBuilder();
            for (Pair<String, Quote> quotePair : quoteList) {
                String nextQuote = quotePair.getValue().getQuoteText() + " - " + quotePair.getKey() + ", " + quotePair.getValue().getQuoteYear();
                if ((sb.toString().length() + nextQuote + "\n\n").length() < 4096)
                    sb.append(nextQuote + "\n\n");
                else {
                    //In case the first embed Descriptions is full (limit of 4096), start another one and create a new StringBuilder instance
                    //The description gets stored in the embedDescription list
                    embedDescriptions.add(sb.toString());
                    sb = new StringBuilder(nextQuote + "\n\n");
                }
            }
            //After every quote is done, add the last string in the StringBuilder to the list
            //But only if the length of the string is greater than one
            if (sb.toString().length() > 0)
                embedDescriptions.add(sb.toString());
            //Just yeet them out there
            for (String embedDescription : embedDescriptions) {
                message.getChannel().block().createMessage(y -> y.setEmbed(x -> x.setTitle(embedTitle).setDescription(embedDescription).setColor(Color.of(0, 102, 102)))).block();
            }
        }
    }
}
