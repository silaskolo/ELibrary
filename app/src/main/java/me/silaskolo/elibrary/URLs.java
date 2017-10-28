package me.silaskolo.elibrary;

/**
 * Created by tolujimoh on 26/10/2017.
 */


public class URLs {

    private static final String AUTH_URL = "http://silaskolo.me/elibrary/service/user.php?action=";
    private static final String BOOK_URL = "http://silaskolo.me/elibrary/service/books.php?action=";

    public static final String URL_REGISTER = AUTH_URL + "register";
    public static final String URL_LOGIN= AUTH_URL + "login";
    public static final String URL_BOOK_RECOMMENDED = BOOK_URL + "recommended";
    public static final String URL_BOOK_NEW = BOOK_URL + "new";
    public static final String URL_BOOK_BY_ID = BOOK_URL + "preview&bookID=" ;
    public static final String BOOK_PATH = "http://silaskolo.me/elibrary/uploads/books/";

}