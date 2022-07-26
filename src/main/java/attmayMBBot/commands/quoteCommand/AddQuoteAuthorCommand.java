package attmayMBBot.commands.quoteCommand;

import attmayMBBot.authorization.AdvancedBotUserAuthorization;
import attmayMBBot.commands.ICommand;
import attmayMBBot.config.AttmayMBBotConfig;
import attmayMBBot.functionalities.quoteManagement.QuoteAuthor;
import attmayMBBot.functionalities.quoteManagement.QuoteManager;
import discord4j.core.object.entity.Message;

public class AddQuoteAuthorCommand implements ICommand {
    private AttmayMBBotConfig config;
    private QuoteManager quoteManager;

    public AddQuoteAuthorCommand(AttmayMBBotConfig config, QuoteManager quoteManager) {
        this.config = config;
        this.quoteManager = quoteManager;
    }

    @Override
    public void execute(Message message, String[] args) {
        //This is a command that you need to be authorized for in order to perform it. Luckily Past-Woodmaninantor built a class for this very thing
        if(new AdvancedBotUserAuthorization(this.config).checkIfUserIsAuthorized(message.getAuthor().get())){
            if(args.length == 3){
                String quoteAuthorName = args[1];
                String discordIdString = args[2];
                Long discordId = 0L;

                //I'm too lazy to look for an equivalent method of TryParse in Java, so I'm just going to use a try/catch block
                try {
                    discordId = Long.parseLong(discordIdString);
                } catch(Exception ex){
                    message.getChannel().block().createMessage("The discord ID is not a valid number!").block();
                    return;
                }

                if(!this.quoteManager.checkIfQuoteAuthorNameExists(quoteAuthorName)){
                    //Name does not exist, create a new QuoteAuthor object
                    QuoteAuthor newQuoteAuthor = new QuoteAuthor(quoteAuthorName, discordId);
                    this.quoteManager.getQuoteAuthors().add(newQuoteAuthor);
                    this.quoteManager.saveQuotesToFile();
                    message.getChannel().block().createMessage("Quote author successfully added!").block();
                } else {
                    //Name already exists, print an error
                    message.getChannel().block().createMessage("Quote author can not be added because that user is already registered!").block();
                }
            } else {
                //Wrong number of arguments
                message.getChannel().block().createMessage("This command feels incomplete.\nUse !addAuthor [Username] [Discord ID of the user] instead!").block();
            }
        } else //No access to the command
            message.getChannel().block().createMessage("Well, I know this is somewhat awkward but you are not allowed to perform this command.").block();
    }
}
