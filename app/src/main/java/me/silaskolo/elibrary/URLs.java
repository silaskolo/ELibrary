package me.silaskolo.elibrary;

public class URLs {

    private static final String APP_ROOT = "http://silaskolo.me/elibrary/";
    private static final String AUTH_URL = APP_ROOT + "service/user.php?action=";
    private static final String BOOK_URL = APP_ROOT + "service/books.php?action=";
    private static final String CATEGORY_URL = APP_ROOT + "service/category.php?action=";
    private static final String AUTHOR_URL = APP_ROOT + "service/author.php?action=";
    private static final String PREFERENCE_URL = APP_ROOT + "service/preference.php?action=";

    public static final String BOOK_PATH = APP_ROOT + "uploads/books/";

    public static final String URL_REGISTER = AUTH_URL + "register";
    public static final String URL_LOGIN= AUTH_URL + "login";
    public static final String URL_UPDATE= AUTH_URL + "update";
    public static final String URL_PROFILE= AUTH_URL + "profile";

    public static final String URL_BOOK_RECOMMENDED = BOOK_URL + "recommended";
    public static final String URL_BOOK_NEW = BOOK_URL + "new";
    public static final String URL_BOOK_BY_ID = BOOK_URL + "preview&bookID=" ;
    public static final String URL_BOOK_SEARCH = BOOK_URL + "search&searchText=" ;
    public static final String URL_BOOK_PERSONAL = BOOK_URL + "personal" ;

    public static final String URL_CATEGORY_ALL = CATEGORY_URL + "all";
    public static final String URL_CATEGORY_BOOKS = CATEGORY_URL + "books&categoryID=";
    public static final String URL_CATEGORY_SEARCH = CATEGORY_URL + "search&searchText=";

    public static final String URL_AUTHOR_SEARCH = AUTHOR_URL + "search&searchText=";

    public static final String URL_PREFERENCE_BOOK = PREFERENCE_URL;

}