package mietzekatze.climbingtracker.dataHandling;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by lisza on 06.10.17.
 */

public class DataBaseContract {

    public static final String CONTENT_AUTHORITY = "mietzekatze.climbingtracker";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_AREAS = "areas";
    public static final String PATH_SUMMITS = "summits";
    public static final String PATH_ROUTES = "routes";
    public static final String PATH_MyROUTES = "myRoutes";
    public static final String PATH_GRADES = "grades" ;

    private DataBaseContract(){};

    public static final class GradeEntry implements BaseColumns{
        public static final Uri GRADES_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_GRADES);
        public static final String TABLE_NAME = "difficulties";
        public static final String GRADE_ID = BaseColumns._ID;
        public static final String COLUMN_SAXONIAN = "saxonian";
        public static final String COLUMN_FRENCH = "french";
        public static final String COLUMN_UIAA = "UIAA";
        public static final String COLUMN_SIERRA = "sierra";
    }

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
        public static final String COLUMN_SUMMIT_NUMBER = "summitNumber";
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

        //define status constants
        public static final int NOT_DONE = 0;
        public static final int DONE_AS_FOLLOWER = 1;
        public static final int DONE_AS_LEADER = 2;
        public static final int SACK = 3;

    }
}