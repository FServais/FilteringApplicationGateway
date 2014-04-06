import html.HTMLPage;


public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String response = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\"> \n" +
				"<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"fr\" >\n" +
			    "<head>\n" +
			        "<title>Mon blog</title>\n" +
			        "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" />\n" +
			        "<link href=\"vue/blog/style.css\" rel=\"stylesheet\" type=\"text/css\" />\n"  +
			    "</head>\n" +
			        
			    "<body>\n" +
			        "<h1>Mon super blog !</h1>\n" +
			        "<p>Derniers billets du blog :</p>\n" +
			 
			    "<div class=\"news\">\n" +
			        "<h3>\n" +
			            "<em>le </em>\n" +
			        "</h3>\n" +
			        
			        "<p>\n" +
			        "<br />\n" +
			        "<em><a href=\"commentaires.php?billet=4\">Commentaires</a></em>\n" +
			        "</p>\n" +
			    "</div>\n" +
			"</body>\n" +
			"</html>\n";
		
		HTMLPage hp = new HTMLPage(response);
		hp.displayTree();
		
	}

}
