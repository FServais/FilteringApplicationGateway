

public class Server
{
	private WordList wordlist;
	private LinkedBlockingQueue<String> msgQueue = null;// Queue for outputting threads messages in std ouput
	private Displayer displayerThread = null;
	private ConfigurationServer configThread = null;
	private HttpServer httpServer = null;

	public Server()
	{
		wordlist = new WordList();

		// initialize displayer thread
		msgQueue = new LinkedBlockingQueue<String>(); 
		displayerThread = Displayer.getInstance();
		displayerThread.setQueue(msgQueue);
		displayerThread.setDaemon(true);
		displayerThread.start();

		// initalize thread for dealing with http connection
		httpServer = new HttpServer(wordlist, msgQueue);
		httpServer.setDaemon(true);
		httpServer.start();

		// initalize thread for dealing with connection to the configuration platform
		configThread = new ConfigurationServer(wordlist, msgQueue);
		configThread.setDaemon(true);
		configThread.start();

	}

	void main(String args[])
	{

	}
}