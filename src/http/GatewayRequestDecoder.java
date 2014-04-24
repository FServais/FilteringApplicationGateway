package http;

/**
 * A class for decoding a request from the client to the gateway
 * @author Romain Mormont
 */
public class GatewayRequestDecoder 
{
	private HTTPRequest http_req = null;
	private String
	public GatewayRequestDecoder(String request)
	{
		http_req = new HTTPRequest(request);
		
	}
}
