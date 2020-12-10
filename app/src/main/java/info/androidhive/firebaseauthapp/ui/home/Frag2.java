    package info.androidhive.firebaseauthapp.ui.home;

    import android.annotation.SuppressLint;
    import android.content.Intent;
    import android.database.Cursor;
    import android.graphics.Color;
    import android.os.Bundle;
    import android.text.SpannableString;
    import android.text.style.ForegroundColorSpan;
    import android.text.style.RelativeSizeSpan;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.BaseExpandableListAdapter;
    import android.widget.Button;
    import android.widget.ExpandableListView;
    import android.widget.TextView;

    import androidx.fragment.app.Fragment;

    import com.github.mikephil.charting.charts.PieChart;
    import com.github.mikephil.charting.components.Legend;
    import com.github.mikephil.charting.data.PieData;
    import com.github.mikephil.charting.data.PieDataSet;
    import com.github.mikephil.charting.data.PieEntry;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;

    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Date;

    import info.androidhive.firebaseauthapp.R;
    import info.androidhive.firebaseauthapp.SQLite.DatabaseHelper;
    import info.androidhive.firebaseauthapp.food.foodClassification;

    import static com.github.mikephil.charting.utils.ColorTemplate.rgb;


    public class Frag2 extends Fragment {
    DatabaseHelper myDb;
    private ExpandableListView user_list ;
    private final ArrayList<ArrayList<String>> list = new ArrayList<>();
    private final ArrayList<ArrayList<String>> list2 = new ArrayList<>();
    private final ArrayList<String> list11 = new ArrayList<>();
    private final ArrayList<Float> list22 = new ArrayList<>();
    private Button bt_eat;
    private TextView tv_food;
    private Integer amount=0;
    private PieChart chart;
    private float x1,x2,x3,x4,x5,x6;
    private final int[] food_color = {rgb("#FFE0B00C"), rgb("#FFDA1FAF"), rgb("#FF5DADE2"), rgb("#FF229954"), rgb("#FFD35400"), rgb("#FF9C6A1B")};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment_frag2 = inflater.inflate(R.layout.fragment_frag2, container, false);
        init(fragment_frag2);

        myDb = new DatabaseHelper(Frag2.super.getContext());
        Cursor res = myDb.getAllData();

        res.getCount();// show message
        //textView.setText("Error Nothing found");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = null;
        if (user != null) {
            uid = user.getUid();
        }

        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
        String str = formatter.format(curDate);

        int meal1 = 0;
        list.add(new ArrayList<>());
        list.get(meal1).add(String.valueOf(new ArrayList<String>()));
        list2.add(new ArrayList<>());
        list2.get(meal1).add(String.valueOf(new ArrayList<String>()));
        while (res.moveToNext()) {
            assert uid != null;
            if(uid.equals(res.getString(4))){
                if(res.getString(1).substring(0, 11).equals(str)){
                    amount+=1;
                    if(res.getInt(5)==(meal1+1)){
                        list.get(meal1).add(res.getString(2));
                        list2.get(meal1).add(res.getString(3));
                    }else {
                        meal1+=1;
                        list.add(new ArrayList<>());
                        list.get(meal1).add(res.getString(2));
                        list2.add(new ArrayList<>());
                        list2.get(meal1).add(res.getString(3));
                    }
                    list11.add(res.getString(2));
                    list22.add(res.getFloat(3));
                }
            }
        }

        if(amount>0){
            user_list.setVisibility(View.VISIBLE); //顯示
            chart.setVisibility(View.VISIBLE); //顯示
            tv_food.setVisibility(View.GONE); // 隱藏

            list.get(0).remove(0);
            list2.get(0).remove(0);

            user_list.setAdapter(new Frag2.MyExpandableListView());

            // Listview on child click listener
            user_list.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> false);

            chart.getDescription().setEnabled(false);

            chart.setCenterText(generateCenterText());
            chart.setCenterTextSize(10f);

            // radius of the center hole in percent of maximum radius
            chart.setHoleRadius(45f);
            chart.setTransparentCircleRadius(50f);

            Legend l = chart.getLegend();
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            l.setOrientation(Legend.LegendOrientation.VERTICAL);
            l.setDrawInside(false);

            chart.setData(generatePieData());
        }else{
            user_list.setVisibility(View.GONE);
            chart.setVisibility(View.GONE);
            tv_food.setVisibility(View.VISIBLE);
        }



        bt_eat.setOnClickListener(view -> startActivity(new Intent(Frag2.super.getContext(), foodClassification.class)));





        return fragment_frag2;
    }
    private SpannableString generateCenterText() {
        SpannableString s = new SpannableString("目前食物紀錄\ntoday");
        s.setSpan(new RelativeSizeSpan(1f), 0, 8, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 8, s.length(), 0);
        return s;
    }

    protected PieData generatePieData() {

        for(int i=0;i<list11.size();i++){
            switch (list11.get(i)) {
                case "全榖雜糧類":
                    x1 = x1 + list22.get(i);
                    break;
                case "豆魚蛋肉類":
                    x2 = x2 + list22.get(i);
                    break;
                case "乳品類":
                    x3 = x3 + list22.get(i);
                    break;
                case "蔬菜類":
                    x4 = x4 + list22.get(i);
                    break;
                case "水果類":
                    x5 = x5 + list22.get(i);
                    break;
                case "油脂與堅果種子類":
                    x6 = x6 + list22.get(i);
                    break;
            }
        }

        ArrayList<PieEntry> entries1 = new ArrayList<>();

        if(x1!=0){
            entries1.add(new PieEntry(x1, "全榖雜糧類"));
        }
        if(x2!=0){
            entries1.add(new PieEntry(x2, "豆魚蛋肉類"));
        }
        if(x3!=0){
            entries1.add(new PieEntry(x3, "乳品類"));
        }
        if(x4!=0){
            entries1.add(new PieEntry(x4, "蔬菜類"));
        }
        if(x5!=0){
            entries1.add(new PieEntry(x5, "水果類"));
        }
        if(x6!=0){
            entries1.add(new PieEntry(x6, "油脂與堅果種子類"));
        }

        PieDataSet ds1 = new PieDataSet(entries1, "");
        ds1.setColors(food_color);
        ds1.setSliceSpace(2f);
        ds1.setValueTextColor(Color.BLACK);
        ds1.setValueTextSize(10f);

        return new PieData(ds1);
    }

    @SuppressLint("WrongViewCast")
    private void init(View v) {
        user_list = v.findViewById(R.id.user_list);
        bt_eat = v.findViewById(R.id.bt_eat);
        chart = v.findViewById(R.id.chart1);
        tv_food = v.findViewById(R.id.tv_food);
    }
    //为ExpandableListView自定义适配器
    class MyExpandableListView extends BaseExpandableListAdapter {

        //返回一级列表的个数
        @Override
        public int getGroupCount() {
            return list.size();
        }

        //返回每个二级列表的个数
        @Override
        public int getChildrenCount(int groupPosition) { //参数groupPosition表示第几个一级列表
            Log.d("smyhvae", "-->" + groupPosition);
            return list.get(groupPosition).size();
        }

        //返回一级列表的单个item（返回的是对象）
        @Override
        public Object getGroup(int groupPosition) {
            return list.get(groupPosition);
        }

        //返回二级列表中的单个item（返回的是对象）
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return list.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        //每个item的id是否是固定？一般为true
        @Override
        public boolean hasStableIds() {
            return true;
        }

        //【重要】填充一级列表
        @SuppressLint({"SetTextI18n", "InflateParams"})
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.today_food_listview,null);
            }
            TextView meal = convertView.findViewById(R.id.meal);

            meal.setText("第"+ (groupPosition + 1) +"餐");



            return convertView;
        }

        //【重要】填充二级列表
        @SuppressLint("InflateParams")
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.today_food, null);
            }
            TextView name = convertView.findViewById(R.id.name);
            TextView amount = convertView.findViewById(R.id.amount);


                name.setText(list.get(groupPosition).get(childPosition));
                amount.setText(list2.get(groupPosition).get(childPosition));
                name.setTextColor(Color.rgb(0,0,0));
                amount.setTextColor(Color.rgb(0,0,0));
                name.setTextSize(20);
                amount.setTextSize(20);

            return convertView;
        }

        //二级列表中的item是否能够被选中？可以改为true
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
