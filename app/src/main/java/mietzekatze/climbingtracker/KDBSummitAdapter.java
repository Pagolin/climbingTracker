package mietzekatze.climbingtracker;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import static mietzekatze.climbingtracker.dataHandling.DataBaseContract.scalesAndGrades;

public class KDBSummitAdapter extends CursorAdapter {
    private Context context;
    private String[] currentScale;

    public KDBSummitAdapter(android.content.Context context, Cursor cursor){
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
        TextView dateSlot = view.findViewById(R.id.date_slot);


        int indexOfID = cursor.getColumnIndexOrThrow("_id");
        int indexOfName = cursor.getColumnIndexOrThrow("summitName");
        int indexOfRouteCount = cursor.getColumnIndexOrThrow("routes_count");

        int s_id = cursor.getInt(indexOfID);
        String Name = cursor.getString(indexOfName);
        int route_count = cursor.getInt(indexOfRouteCount);

        routeAndSummitField.setText(route_count + " Wege");
        areaNameField.setText(Name);
        difficField.setText(String.valueOf(s_id));
        coloredCircle.setColor(selectColor(s_id));
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
