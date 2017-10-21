package mietzekatze.climbingtracker;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mietzekatze.climbingtracker.dataHandling.DataBaseContract;
import mietzekatze.climbingtracker.dataHandling.HTMLParser;

import static mietzekatze.climbingtracker.dataHandling.DataBaseContract.scalesAndGrades;

/**
 * Created by lisza on 08.10.17.
 */

public class MyRoutesCursorAdapter extends CursorAdapter {

    private Context context;
    private String[] currentScale;

    public MyRoutesCursorAdapter(Context context,Cursor cursor){
        super(context,cursor, 0);
        this.context = context;
        currentScale = scalesAndGrades.get(OverviewActivity.currentScalePreference);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parentView) {
        return  LayoutInflater.from(context).inflate(R.layout.route_list_item, parentView, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView routeAndSummitField =  view.findViewById(R.id.route_and_summit_slot);
        TextView areaNameField = view.findViewById(R.id.area_name_slot);
        TextView difficField = view.findViewById(R.id.difficulty_slot);
        GradientDrawable coloredCircle = (GradientDrawable) difficField.getBackground();
        

        int indexOfRoute = cursor.getColumnIndexOrThrow(DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_NAME);
        String route  = cursor.getString(indexOfRoute);
        int indexOfSummit = cursor.getColumnIndexOrThrow(DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_SUMMIT);
        String summit  = cursor.getString(indexOfSummit);
        int indexOfArea = cursor.getColumnIndexOrThrow(DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_AREA);
        String area  = cursor.getString(indexOfArea);
        int indexOfDifficulty = cursor.getColumnIndexOrThrow(DataBaseContract.MyRoutesEntry.COLUMN_ROUTE_DIFFICULTY);
        int difficulty = cursor.getInt(indexOfDifficulty);

        routeAndSummitField.setText(summit + ", "+ route);
        areaNameField.setText(area);
        difficField.setText(currentScale[difficulty]);
        coloredCircle.setColor(selectColor(difficulty));
    }

    private int selectColor(int difficulty) {
        int gradeColorRessource;
        int gradeNormed = norm(difficulty);
        switch (gradeNormed) {
            case 0:
            case 1:
                gradeColorRessource = R.color.diff1;
                break;
            case 2:
                gradeColorRessource = R.color.diff2;
                break;
            case 3:
                gradeColorRessource = R.color.diff3;
                break;
            case 4:
                gradeColorRessource = R.color.diff4;
                break;
            case 5:
                gradeColorRessource = R.color.diff5;
                break;
            case 6:
                gradeColorRessource = R.color.diff6;
                break;
            case 7:
                gradeColorRessource = R.color.diff7;
                break;
            case 8:
                gradeColorRessource = R.color.diff8;
                break;
            case 9:
                gradeColorRessource = R.color.diff9;
                break;
            default:
                gradeColorRessource = R.color.diff10plus;
                break;
        }
        return ContextCompat.getColor(this.context, gradeColorRessource);
    }

    private int norm(int difficulty) {
        float countGrades = currentScale.length;
        float relativeGrade = (difficulty/countGrades) *10;
        return Math.round(relativeGrade);
    }


}
