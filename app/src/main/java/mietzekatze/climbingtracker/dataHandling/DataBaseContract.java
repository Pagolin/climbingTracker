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

    private DataBaseContract(){};

    public static final class AreaEntry implements BaseColumns{
        public static final Uri AREAS_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_AREAS);

        //define column names
        public static final String TABLE_NAME = "areas";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_AREA_NAME = "areaName";
    }

    public static final class SummitEntry implements BaseColumns{
        public static final Uri SUMMITS_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SUMMITS);

        //define column names
        public static final String TABLE_NAME = "summits";

        public static final String _ID = BaseColumns._ID;
        //use an extra column to avoid aliasing the _ID in SELECT staments of CursorAdapter
        public static final String COLUMN_SUMMIT_NUMBER = "summitNumber";
        public static final String COLUMN_SUMMIT_NAME = "summitName";
        public static final String COLUMN_SUMMIT_AREA = "summitArea";
        public static final String COLUMN_SUMMIT_GEOTAG ="summitGeotag";
    }

    public static final class RoutesEntry implements BaseColumns{
        public static final Uri ROUTES_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ROUTES);

        //define column names
        public static final String TABLE_NAME = "routes";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_ROUTES_NAME = "routeName";
        public static final String COLUMN_ROUTES_SUMMIT_ID = "routeSummit";
        public static final String COLUMN_ROUTES_DIFFICULTY = "difficulty";
        public static final String COLUMN_ROUTES_STATUS ="routeStatus";

        //define status constants
        public static final int NOT_DONE = 0;
        public static final int DONE_AS_FOLLOWER = 1;
        public static final int DONE_AS_LEADER = 2;
        public static final int SACK = 3;

    }

    public static final class MyRoutesEntry implements BaseColumns{
        public static final Uri MyROUTES_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MyROUTES);

        //define column names
        public static final String TABLE_NAME = "myRoutes";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_ROUTE_NAME = "routeName";
        public static final String COLUMN_ROUTE_SUMMIT = "summitName";
        public static final String COLUMN_ROUTE_AREA = "areaName";
        public static final String COLUMN_ROUTE_DIFFICULTY = "difficulty";
        public static final String COLUMN_ROUTE_STATUS ="routeStatus";

        //define status constants
        public static final int NOT_DONE = 0;
        public static final int DONE_AS_LEADER = 1;
        public static final int DONE_AS_FOLLOWER = 2;
        public static final int SACK = 3;

    }
}