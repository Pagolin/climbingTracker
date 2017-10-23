package mietzekatze.climbingtracker.dataHandling;

import android.net.Uri;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mietzekatze.climbingtracker.dataHandling.DataBaseContract.BASE_CONTENT_URI;
import static mietzekatze.climbingtracker.dataHandling.DataBaseContract.PATH_AREAS;
import static mietzekatze.climbingtracker.dataHandling.DataBaseContract.PATH_MyROUTES;
import static mietzekatze.climbingtracker.dataHandling.DataBaseContract.PATH_ROUTES;
import static mietzekatze.climbingtracker.dataHandling.DataBaseContract.PATH_SUMMITS;

/**
 * Created by lisza on 06.10.17.
 * Beside the database schema as provided by the Databaseprovider some constant String values and
 * the constant mapping of climbing scales and according grades are stored here
 */

public class DataBaseContract {

    public static final String CONTENT_AUTHORITY = "mietzekatze.climbingtracker";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_AREAS = "areas";
    public static final String PATH_SUMMITS = "summits";
    public static final String PATH_ROUTES = "routes";
    public static final String PATH_MyROUTES = "myRoutes";
    public static final String PATH_GRADES = "grades" ;
    public static final String SCALE_SAX = "Sächsisch";
    public static final String SCALE_FRENCH = "Französisch";
    public static final String SCALE_UIAA = "UIAA";
    public static final String SCALE_SIERRA = "Sierra" ;
    public static final HashMap<String, String[]> scalesAndGrades = new HashMap<String, String[]>(){{
        put("Britisch (Adj)", new String[]{"M", "M/D", "D",
                "D/VD", "VD", "S", "HS", "HS/VS","VS", "HVS",
                "E1", "E1/E2", "E2", "E2/E3", "E3", "E3/E4",
                "E4", "E4/E5", "E5", "E5/E6", "E6", "E6/E7","E7",
                "E7/E8", "E8", "E9", "E9/E10", "E10", "E11", "E11",
                "> E11", "> E11", "> E11", "> E11", "> E11", "> E11"});
        put("Sächsisch", new String[]{"I", "II", "III", "IV", "IV/V", "V", "VI",
                "VI/VIIa", "VIIa", "VIIb", "VIIc", "VIIIa","VIIIb",
                "VIIIc","VIIIc/IXa","IXa","IXb","IXc","IXc/Xa","Xa",
                "Xb","Xc","Xc","Xc/XIa","XIa","XIb","XIc","XIc/XIIa",
                "XIIa","XIIb","XIIb","XIIb/XIIc","XIIc","XIIc","> XIIc","> XIIc"});
        put("Sierra", new String[]{"5.0", "5.1", "5.2", "5.3", "5.4", "5.5", "5.6",
                "5.7","5.8","5.9","5.10a","5.10b","5.10c","5.10d",
                "5.11a","5.11b","5.11c","5.11d","5.12a","5.12b",
                "5.12c","5.12d","5.13a","5.13b","5.13c","5.13d",
                "5.14a","5.14b","5.14c","5.14d","5.14d/5.15a",
                "5.15a", "5.15a/5.15b", "5.15b", "5.15c", "5.15d"});
        put("Französisch", new String[]{"1", "2", "3", "4", "4+", "5a", "5a/5b", "5b",
                "5b/5c", "5c", "6a", "6a+", "6b", "6b+", "6c", "6c+",
                "7a", "7a+", "7b", "7b+", "7c", "7c+", "8a", "8a/8a+",
                "8a+", "8b", "8b+", "8c", "8c+", "9a", "9a/9a+",
                "9a+", "9a+/9b", "9b", "9b+", "9c"});
        put("UIAA", new String[]{"1", "2", "3", "4", "4+", "5-", "5", "5+", "6-", "6",
                "6+", "7-", "7", "7+", "7+/8-", "8-", "8", "8+",
                "8+/9-", "9-", "9", "9+", "9+/10-", "10-", "10-",
                "10", "10+", "10+/11-", "11-", "11", "11", "11/11+",
                "11+", "11+/12-", "12-", "12"});
        put("Britisch (Tech)", new String[]{"1", "2", "3", "4a", "4a", "4a/4b", "4b",
                "4c", "4c/5a", "5a", "5a/5b", "5b", "5b/5c", "5c",
                "5c", "5c/6a", "6a", "6a", "6a/6b", "6b", "6b/6c",
                "6c", "6c", "6c/7a", "7a", "7a", "7a/7b", "7b",
                "7b", "7b", ">7b", ">7b", ">7b", ">7b", ">7b",">7b"});

    }};

    public DataBaseContract(){}


    public static final class AreaEntry implements BaseColumns{
        public static final Uri AREAS_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_AREAS);
        public static final String TABLE_NAME = "areas";
        public static final String AREA_ID = BaseColumns._ID;
        public static final String COLUMN_AREA_NAME = "areaName";
    }

    public static final class SummitEntry implements BaseColumns{
        public static final Uri SUMMITS_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SUMMITS);
        public static final String TABLE_NAME = "summits";
        public static final String SUMMIT_ID = BaseColumns._ID;
        public static final String COLUMN_SUMMIT_NAME = "summitName";
        public static final String COLUMN_SUMMIT_AREA = "summitArea";
        public static final String COLUMN_SUMMIT_GEOTAG ="summitGeotag";
    }

    public static final class RoutesEntry implements BaseColumns{
        public static final Uri ROUTES_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ROUTES);
        public static final String TABLE_NAME = "routes";
        public static final String ROUTE_ID = BaseColumns._ID;
        public static final String COLUMN_ROUTES_NAME = "routeName";
        public static final String COLUMN_ROUTES_SUMMIT_ID = "routeSummit";
        public static final String COLUMN_ROUTES_DIFFICULTY = "difficulty";
        //Securing as int from 1 t
        public static final String COLUMN_ROUTES_SECURING = "securing";
        public static final String COLUMN_ROUTE_RATING = "rating";
        public static final String COLUMN_ROUTE_DIRECTION = "direction";

    }

    public static final class MyRoutesEntry implements BaseColumns{
        public static final Uri MyROUTES_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MyROUTES);
        public static final String TABLE_NAME = "myRoutes";
        public static final String MyROUTE_ID = BaseColumns._ID;
        public static final String COLUMN_ROUTE_NAME = "routeName";
        public static final String COLUMN_ROUTE_SUMMIT = "summitName";
        public static final String COLUMN_ROUTE_AREA = "areaName";
        public static final String COLUMN_ROUTE_DIFFICULTY = "difficulty";
        public static final String COLUMN_ROUTE_STATUS ="routeStatus";
        public static final String COLUMN_ROUTE_DATE = "dateClimbed";

        //define status constants
        public static final int NOT_DONE = 0;
        public static final int DONE_AS_FOLLOWER = 1;
        public static final int DONE_AS_LEADER = 2;
        public static final int SACK = 3;
    }
}