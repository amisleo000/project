package info.androidhive.firebaseauthapp.ui.dashboard;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import info.androidhive.firebaseauthapp.BodyInformation.ChartItem;
import info.androidhive.firebaseauthapp.BodyInformation.LineChartItem;
import info.androidhive.firebaseauthapp.BodyInformation.LineChartItem2;
import info.androidhive.firebaseauthapp.FastRecordsActivity;
import info.androidhive.firebaseauthapp.R;
import info.androidhive.firebaseauthapp.SQLite.BodyRecord;
import info.androidhive.firebaseauthapp.SQLite.PersonalInformation;

/**
 * Created by Belal on 1/23/2018.
 */
//Intent accountIntent = new Intent(DashboardFragment.super.getContext(), Weight_scale.class);
//Intent accountIntent = new Intent(DashboardFragment.super.getContext(), Food_Record.class);
public class DashboardFragment extends Fragment {

    private LineChart weight_chart,bmi_chart;
    private TextView tv_current_weight,tv_current_bmi ,tv_init_weight,tv_ideal_weight;
    private ImageView btn_fast_record,btn_food_record,btn_weight_add,btn_waist_add,btn_fat_add;
    private TextView tv_target_weight,tv_weight_diff,tv_health_weight_range;
    private TextView tv_bmr,tv_tdee;
    private ImageView tdee_info,bmr_info;
    private ImageView work_lite,work_medium,work_heavy;
    private TextView tv_daily_activity;
    ListView lv;
    String[] exercise = {"久坐","輕量活動","中度活動量","高度活動量","非常高度活動量"};
    String[] exercise_describe = {"基本沒在運動","每周運動1-3天","每周運動3-5天","每周運動6-7天","勞力密集的工作或是每天進行訓練"};

    PersonalInformation myDb;
    BodyRecord myDb2;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = null;
    //arrayList用來存從myDb2讀下來的資料
    private ArrayList<Float> KGs = new ArrayList<>();
    private ArrayList<Float> Heights = new ArrayList<>();
    private ArrayList<Float> Waists = new ArrayList<>();
    private ArrayList<Float> Body_fats = new ArrayList<>();
    private ArrayList<String> Dates = new ArrayList<>();
    private ArrayList<Integer> IDs = new ArrayList<>();


    private PopupWindow popupWindow;
    private View workingAge_view;

    private int user_exercise_level;
    private String gender;
    private int age ;

    float bmr;
    float tdee;

    private float weight_data;
    private float height = 0;
    private float waist =0;
    private float fat = 0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment_dashboard = inflater.inflate(R.layout.fragment_dashboard, container, false);
        init(fragment_dashboard);

        btn_fast_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), FastRecordsActivity.class));
            }
        });
        btn_weight_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(fragment_dashboard.getContext(),Weight_scale.class));
                startDialog(0);
            }
        });
        btn_waist_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialog(1);
            }
        });
        btn_fat_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialog(2);
            }
        });
        btn_food_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),Food_Record.class));
            }
        });
        uid = user.getUid();

        myDb = new PersonalInformation(getContext());
        Cursor res = myDb.getAllData();
        while (res.moveToNext()) {
            if(uid.equals(res.getString(0))){
                user_exercise_level = res.getInt(7);
                gender = res.getString(1);
                age = res.getInt(2);
            }
        }

        //將BodyRecord中的所有資料抓下來
        Cursor res2 = myDb2.getAllData();
        if (res2.getCount()<=0){
            Log.e("no data found","no data in res2");

        }else {
            while (res2.moveToNext()) {
                if(uid.equals(res2.getString(1))){
                    IDs.add(res2.getInt(0));
                    KGs.add(res2.getFloat(2));
                    Heights.add(res2.getFloat(3));
                    Waists.add(res2.getFloat(4));
                    Body_fats.add(res2.getFloat(5));
                    Dates.add(res2.getString(6));


                    Log.e("get data ","IDs.size="+IDs.size()+",KGs.size="+KGs.size()+",Dates.size="+Dates.size());
                    Log.e("dates.get(0)",Dates.get(0));
                }
            }
        }
        Log.e("get kgs length",KGs.size()+"");
        Log.e("get waists length",Waists.size()+"");
        height = Heights.get(Heights.size()-1);
        waist= Waists.get(Waists.size()-1);
        fat = Body_fats.get(Body_fats.size()-1);
        setUiData();


        ArrayList<ChartItem> list = new ArrayList<>();
        list.add(new LineChartItem(generateDataLine(1), getContext()));
        list.add(new LineChartItem2(generateDataLine2(2), getContext()));
        list.add(new LineChartItem(generateDataLine3(1),getContext()));
        list.add(new LineChartItem(generateDataLine4(1),getContext()));
        ChartDataAdapter cda = new ChartDataAdapter(getContext(), list);
        lv.setAdapter(cda);
//        try {
//            setUpLineChart();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        return fragment_dashboard;
    }

    private LineData generateDataLine(int cnt) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.fade_blue);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = null;
        if (user != null) {
            uid = user.getUid();
        }

        ArrayList<Entry> values1 = new ArrayList<>();


        for (int i = 0; i < 33; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH,i-30);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String dateStr = sdf.format(calendar.getTime());
            for(int j = 0;j<Dates.size();j++){
                if(dateStr.equals(Dates.get(j))){
                    //Log.e("get i","i = "+i);
                    values1.add(new Entry(i, KGs.get(j)));
                }
            }
        }

        LineDataSet d1 = new LineDataSet(values1, "體重變化");
        d1.setLineWidth(0f);
        //d1.setCircleRadius(0);
        d1.setDrawValues(true);
        d1.setDrawFilled(true);
        d1.setCircleColor(Color.rgb(125, 245, 237));//圓點顏色
        //d1.setCircleRadius(15);//圓點大小
        d1.setDrawCircleHole(false);//圓點為實心(預設空心)
        d1.setFillDrawable(drawable);
        ArrayList<Entry> values2 = new ArrayList<>();

        float good_kg = (height/100)*(height/100)*22;
        for (int i = 0; i < 33; i++) {
            values2.add(new Entry(i, good_kg));
        }

        LineDataSet d2 = new LineDataSet(values2, "理想體重");
        d2.setLineWidth(2.5f);
        d2.setCircleRadius(4.5f);
        d2.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        d2.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        d2.setDrawValues(false);
        d2.setDrawCircles(false);
        d2.setFillDrawable(drawable);

        ArrayList<ILineDataSet> sets = new ArrayList<>();

        sets.add(d1);
        sets.add(d2);

        return new LineData(sets);
    }
    private LineData generateDataLine3(int cnt) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.fade_pink);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = null;
        if (user != null) {
            uid = user.getUid();
        }
        ArrayList<Entry> values1 = new ArrayList<>();
        Log.e("line data KGS" ,KGs.size()+"");
        Log.e("line data waist" ,Waists.size()+"");

        for (int i = 0; i < 33; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH,i-30);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String dateStr = sdf.format(calendar.getTime());
            for(int j = 0;j<Dates.size();j++){
                if(dateStr.equals(Dates.get(j))){
                    //Log.e("get i","i = "+i);
                    values1.add(new Entry(i, Waists.get(j)));
                }
            }
        }

        LineDataSet d1 = new LineDataSet(values1, "腰圍變化");
        d1.setLineWidth(0f);
        //d1.setCircleRadius(0);
        d1.setDrawValues(true);
        d1.setDrawFilled(true);
        d1.setCircleColor(Color.rgb(125, 245, 237));//圓點顏色
        //d1.setCircleRadius(15);//圓點大小
        d1.setDrawCircleHole(false);//圓點為實心(預設空心)
        d1.setFillDrawable(drawable);

        ArrayList<ILineDataSet> sets = new ArrayList<>();

        sets.add(d1);


        return new LineData(sets);
    }
    private LineData generateDataLine4(int cnt) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.fade_purple);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = null;
        if (user != null) {
            uid = user.getUid();
        }
        ArrayList<Entry> values1 = new ArrayList<>();
        Log.e("line data KGS" ,KGs.size()+"");
        Log.e("line data waist" ,Waists.size()+"");

        for (int i = 0; i < 33; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH,i-30);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String dateStr = sdf.format(calendar.getTime());
            for(int j = 0;j<Dates.size();j++){
                if(dateStr.equals(Dates.get(j))){
                    //Log.e("get i","i = "+i);
                    values1.add(new Entry(i, Body_fats.get(j)));
                }
            }
        }

        LineDataSet d1 = new LineDataSet(values1, "體脂肪變化");
        d1.setLineWidth(0f);
        //d1.setCircleRadius(0);
        d1.setDrawValues(true);
        d1.setDrawFilled(true);
        d1.setCircleColor(Color.rgb(125, 245, 237));//圓點顏色
        //d1.setCircleRadius(15);//圓點大小
        d1.setDrawCircleHole(false);//圓點為實心(預設空心)
        d1.setFillDrawable(drawable);

        ArrayList<ILineDataSet> sets = new ArrayList<>();

        sets.add(d1);


        return new LineData(sets);
    }

    //產生bmi的摺線圖
    private LineData generateDataLine2(int cnt) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.fade_deep_blue);
        ArrayList<Entry> values1 = new ArrayList<>();

        for (int i = 0; i < 33; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH,i-30);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String dateStr = sdf.format(calendar.getTime());
            for(int j = 0;j<Dates.size();j++){
                if(dateStr.equals(Dates.get(j))){
                    float bmi =  KGs.get(j)/((height/100)*(height/100));
                    //Log.e("得到bmi",""+bmi);
                    values1.add(new Entry(i,bmi));
                }
            }
        }

        LineDataSet d1 = new LineDataSet(values1, "BMI變化");
        d1.setLineWidth(0f);
        d1.setCircleRadius(0);
        d1.setDrawValues(true);
        d1.setDrawFilled(true);
        d1.setCircleColor(Color.rgb(125, 245, 237));//圓點顏色
        d1.setCircleRadius(5);//圓點大小
        d1.setDrawCircleHole(false);//圓點為實心(預設空心)
        d1.setFillDrawable(drawable);

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(d1);

        return new LineData(sets);
    }
    private void startDialog(int type) {
        initNumberPicker3(type);
        // 强制隐藏键盘
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

        // 填充布局并设置弹出窗体的宽,高
        popupWindow = new PopupWindow(workingAge_view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置弹出窗体可点击
        popupWindow.setFocusable(true);
        // 设置弹出窗体动画效果
        popupWindow.setAnimationStyle(R.style.AnimBottom);
        // 触屏位置如果在选择框外面不銷毀
        popupWindow.setOutsideTouchable(true);
        // 设置弹出窗体的背景
        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popupWindow.showAtLocation(workingAge_view, Gravity.CENTER, 0, 0);

        // 设置背景透明度
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.5f;
        getActivity().getWindow().setAttributes(lp);
        // 添加窗口关闭事件
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1f;
                getActivity().getWindow().setAttributes(lp);
            }

        });
    }
    //開啟替重身高選擇器
    private void initNumberPicker3(int type) {
        workingAge_view = LayoutInflater.from(getContext()).inflate(R.layout.show_yourkg2, null);

        final TextView date_picker = workingAge_view.findViewById(R.id.date_picker);
        Button kg_ok = workingAge_view.findViewById(R.id.kg_ok);
        Button btn_setHeight = workingAge_view.findViewById(R.id.btn_setHeight);
        Button btn_setFat= workingAge_view.findViewById(R.id.btn_setFat);
        final TextView editTextWeight = workingAge_view.findViewById(R.id.tv_edit_kg);

        LinearLayout lv_weight_input = workingAge_view.findViewById(R.id.lv_weight_input);
        LinearLayout lv_waist_input = workingAge_view.findViewById(R.id.lv_waist_input);
        LinearLayout lv_fat_input = workingAge_view.findViewById(R.id.lv_fat_input);
        Date date = new Date();
        //DecimalFormat df = new DecimalFormat("##0.0");
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        date_picker.setText(df.format(date));

        btn_setHeight.setText(""+Waists.get(Waists.size()-1));
        btn_setFat.setText(""+Body_fats.get(Body_fats.size()-1));
        Log.e("get type",type+"");
        switch (type){
            case 0:
                lv_weight_input.setVisibility(View.VISIBLE);
                lv_waist_input.setVisibility(View.GONE);
                lv_fat_input.setVisibility(View.GONE);
                break;
            case 1:
                lv_weight_input.setVisibility(View.GONE);
                lv_waist_input.setVisibility(View.VISIBLE);
                lv_fat_input.setVisibility(View.GONE);
                break;
            case 2:
                lv_weight_input.setVisibility(View.GONE);
                lv_waist_input.setVisibility(View.GONE);
                lv_fat_input.setVisibility(View.VISIBLE);
                break;
        }

        editTextWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                final View view = inflater.inflate(R.layout.show_yourkg, null);

                new AlertDialog.Builder(getContext())
                        .setTitle("請輸入你的體重")
                        .setView(view)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = (EditText) (view.findViewById(R.id.editText1));
                                DecimalFormat fdf = new DecimalFormat("###0.##");

                                if(editText.getText().toString().equals("")|| editText.getText().toString().trim().equals("0")) {
                                    LayoutInflater inflater = LayoutInflater.from(getContext());
                                    final View v = inflater.inflate(R.layout.empty_value_alert, null);
                                    new AlertDialog.Builder(getContext())
                                            .setTitle("你tm體重0是不是!")
                                            .setView(v)
                                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            })
                                            .show();
                                    editTextWeight.setText("");
                                }else{
                                    float data = Float.parseFloat(editText.getText().toString());
                                    editTextWeight.setText(fdf.format(data));
                                }
                            }
                        })
                        .show();
            }
        });
        //選擇腰圍按鈕
        btn_setHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                final View view = inflater.inflate(R.layout.height_input_dialog, null);
                new AlertDialog.Builder(getContext())
                        .setTitle("請輸入你的腰圍")
                        .setView(view)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = (EditText) (view.findViewById(R.id.edit_height));
                                if(editText.getText().toString().equals("")|| editText.getText().toString().trim().equals("0")) {
                                    LayoutInflater inflater = LayoutInflater.from(getContext());
                                    final View v = inflater.inflate(R.layout.empty_value_alert, null);
                                    new AlertDialog.Builder(getContext())
                                            .setTitle("你腰圍0是不是!")
                                            .setView(v)
                                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            })
                                            .show();
                                    waist = Waists.get(Waists.size()-1);
                                    Log.e("waist 空值",waist+"");
                                }else{
                                    btn_setHeight.setText(editText.getText().toString());
                                    waist = Float.parseFloat(editText.getText().toString());
                                }
                            }
                        })
                        .show();

            }
        });

        btn_setFat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                final View view = inflater.inflate(R.layout.height_input_dialog, null);
                new AlertDialog.Builder(getContext())
                        .setTitle("請輸入你的體脂")
                        .setView(view)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = (EditText) (view.findViewById(R.id.edit_height));
                                if(editText.getText().toString().equals("")|| editText.getText().toString().trim().equals("0")) {
                                    LayoutInflater inflater = LayoutInflater.from(getContext());
                                    final View v = inflater.inflate(R.layout.empty_value_alert, null);
                                    new AlertDialog.Builder(getContext())
                                            .setTitle("你體脂0是不是!")
                                            .setView(v)
                                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            })
                                            .show();
                                    fat = Body_fats.get(Body_fats.size()-1);
                                    Log.e("fat 空值",fat+"");
                                }else{
                                    btn_setFat.setText(editText.getText().toString());
                                    fat = Float.parseFloat(editText.getText().toString());
                                }
                            }
                        })
                        .show();
            }
        });

        //選擇日期按鈕
        date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog =new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR,year);
                        calendar.set(Calendar.MONTH,month);
                        calendar.set(Calendar.DAY_OF_MONTH,day);
                        Date getDate = calendar.getTime();

                        //String dateTime =String.format("%02d", year)+"/"+String.format("%02d", month+1)+"/"+String.format("%02d", day);
                        date_picker.setText(df.format(getDate));
                    }
                }, year, month, day);
                //设置起始日期和结束日期
                DatePicker datePicker = dialog.getDatePicker();
                Date date = new Date();//当前时间
                long time = date.getTime();
                datePicker.setMaxDate(time);
                dialog.show();
            }
        });
        //體重輸入完成完成
        kg_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
                    Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
                    String str = df.format(curDate);
                    if(date_picker.getText().toString().equals(str)){
                        Log.e("get weight",""+editTextWeight.getText().toString());
                    }

                    if (!editTextWeight.getText().toString().trim().equals("")){
                        weight_data = Float.parseFloat(editTextWeight.getText().toString());
                    }else{
                        weight_data = KGs.get(KGs.size()-1);
                    }


                    try {
                        AddData(date_picker.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                popupWindow.dismiss();
            }
        });
    }


    //設定ui的資料
    private void setUiData() {
        //使用者最開始輸入的體重(使用者最久以前輸入的體重)
        tv_init_weight.setText(String.valueOf(KGs.get(0)));
        //根據使用者最新的體重計算理想體重
        float ideal_weight = (Heights.get(Heights.size()-1)/100)*(Heights.get(Heights.size()-1)/100)*22;
        DecimalFormat fdf = new DecimalFormat("###0.##");
        tv_ideal_weight.setText(fdf.format(ideal_weight));
        tv_target_weight.setText(fdf.format(ideal_weight)+"公斤");
        //根據使用者最新的體重及身高計算當前bmi
        float bmi = KGs.get(KGs.size()-1)/((Heights.get(Heights.size()-1)/100)*(Heights.get(Heights.size()-1)/100));
        Log.e("bmi","kg = "+KGs.get(KGs.size()-1)+"，height= "+(Heights.get(Heights.size()-1)/100));
        Log.e("bmi",""+bmi);
        tv_current_bmi.setText(fdf.format(bmi));
        //取得使用者最新的體重資料作為當前體重
        tv_current_weight.setText(fdf.format(KGs.get(KGs.size()-1))+" 公斤");

        //計算理想體重範圍
        double Upper_limit_weight = ideal_weight+(ideal_weight*0.1);
        double Lower_limit_weight = ideal_weight-(ideal_weight*0.1);

        tv_health_weight_range.setText(fdf.format(Lower_limit_weight)+"~"+fdf.format(Upper_limit_weight)+"\n公斤");

        float weight_diff = 0;

        //如果當前體重小於理想體重
        if (KGs.get(KGs.size()-1)<Lower_limit_weight){
            weight_diff = ideal_weight-KGs.get(KGs.size()-1);
            tv_weight_diff.setText("過輕"+fdf.format(weight_diff)+"公斤");
        }
        //如果當前體重介於理想體重之間
        else if (Upper_limit_weight > KGs.get(KGs.size()-1) && Lower_limit_weight <= KGs.get(KGs.size()-1)){
            tv_weight_diff.setText("恭喜，標準體重");
        }
        else {
            weight_diff = KGs.get(KGs.size()-1)-ideal_weight;
            tv_weight_diff.setText("過重"+fdf.format(weight_diff)+"公斤");
        }

        if (gender.equals("男性")){
            bmr= (float) ((10 *KGs.get(KGs.size()-1))+( 6.25 *Heights.get(Heights.size()-1))-( 5*age)+5);
        }else{
            bmr= (float) ((13.7*KGs.get(KGs.size()-1))+(5.0*Heights.get(Heights.size()-1))-(6.8*age)-161);
        }


        switch(user_exercise_level){
            case 0:
                tdee = (float) (1.2 * bmr);
                break;
            case 1:
                tdee = (float) (1.375 * bmr);
                break;

            case 2:
                tdee = (float) (1.55 * bmr);
                break;

            case 3:
                tdee = (float) (1.725 * bmr);
                break;
            case 4:
                tdee = (float) (1.9 * bmr);
                break;
        }

        tv_bmr.setText("每日基礎代謝 BMR: "+fdf.format(bmr)+" kcal/day");
        tv_tdee.setText("總熱量消耗 TDEE: "+fdf.format(tdee)+" kcal/day");


        tv_daily_activity.setText("每日活動量:"+exercise[user_exercise_level]);

    }

    //新增&更新資料
    private void AddData(String date) throws ParseException {
        Log.e("date get:",""+date);
        //如果資料不存在
        //取得的日期字串轉換成Date
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        Date getDate = df.parse(date);
        long timestamp = getDate.getTime();
        //如果該日期沒有資料，建立一筆日期為今天的新資料
        if(Dates.indexOf(date) ==-1){
            //使用者有輸入資料
            boolean isInserted2 = myDb2.insertData(uid,weight_data,Heights.get(Heights.size()-1),waist,fat,date,timestamp);
            boolean updatePersonal = myDb.updateBodyData(uid,Heights.get(Heights.size()-1),weight_data,waist,fat);
            Log.e("insert: 3 ",""+isInserted2);
            Log.e("update user body info: 3 ",""+updatePersonal);
        }
        //如果資料存在，就更新
        else{
            boolean isupdated = myDb2.updateWeightData(IDs.get(Dates.indexOf(date)), weight_data,waist,fat,timestamp);
            boolean updatePersonal = myDb.updateBodyData(uid,Heights.get(Heights.size()-1),weight_data,waist,fat);
            Log.e("insert: 5 ",""+isupdated);
            Log.e("update user body info: 5 ",""+updatePersonal);
        }
        KGs = new ArrayList<>();
        Heights = new ArrayList<>();
        Waists = new ArrayList<>();
        Body_fats = new ArrayList<>();
        Dates = new ArrayList<>();
        IDs = new ArrayList<>();

        Cursor res2 = myDb2.getAllData();
        while (res2.moveToNext()) {
            if(uid.equals(res2.getString(1))){
                IDs.add(res2.getInt(0));
                KGs.add(res2.getFloat(2));
                Heights.add(res2.getFloat(3));
                Waists.add(res2.getFloat(4));
                Body_fats.add(res2.getFloat(5));
                Dates.add(res2.getString(6));
            }
        }
        setUiData();
        ArrayList<ChartItem> list = new ArrayList<>();
        list.add(new LineChartItem(generateDataLine(1), getContext()));
        list.add(new LineChartItem2(generateDataLine2(2), getContext()));
        list.add(new LineChartItem(generateDataLine3(1),getContext()));
        list.add(new LineChartItem(generateDataLine4(1),getContext()));

        ChartDataAdapter cda = new ChartDataAdapter(getContext(), list);
        lv.setAdapter(cda);
    }

    private void init(View v) {
//        weight_chart = v.findViewById(R.id.weight_chart);
//        bmi_chart = v.findViewById(R.id.bmi_chart);
        tv_current_weight = v.findViewById(R.id.tv_current_weight);
        tv_current_bmi = v.findViewById(R.id.tv_current_bmi);
        tv_init_weight = v.findViewById(R.id.tv_init_weight);
        tv_ideal_weight = v.findViewById(R.id.tv_ideal_weight);
        btn_fast_record = v.findViewById(R.id.btn_fast_record);
        btn_food_record = v.findViewById(R.id.btn_food_record);
        tv_target_weight = v.findViewById(R.id.tv_target_weight);
        tv_weight_diff = v.findViewById(R.id.tv_weight_diff);
        tv_health_weight_range = v.findViewById(R.id.tv_health_weight_range);
        tv_bmr = v.findViewById(R.id.tv_bmr);
        tv_tdee = v.findViewById(R.id.tv_tdee);
        bmr_info = v.findViewById(R.id.bmr_info);
        tdee_info = v.findViewById(R.id.tdee_info);

        tv_daily_activity = v.findViewById(R.id.tv_daily_activity);
        btn_weight_add = v.findViewById(R.id.btn_weight_add);
        btn_waist_add = v.findViewById(R.id.btn_waist_add);
        btn_fat_add = v.findViewById(R.id.btn_fat_add);

        lv=v.findViewById(R.id.list_body_charts);


        myDb = new PersonalInformation(v.getContext());
        myDb2 = new BodyRecord(v.getContext());
    }
    private static class ChartDataAdapter extends ArrayAdapter<ChartItem> {

        ChartDataAdapter(Context context, List<ChartItem> objects) {
            super(context, 0, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            //無檢查ConstantConditions
            return getItem(position).getView(position, convertView, getContext());
        }

        @Override
        public int getItemViewType(int position) {
            // 返回視圖類型
            ChartItem ci = getItem(position);
            return ci != null ? ci.getItemType() : 0;
        }

        @Override
        public int getViewTypeCount() {
            return 3; // 我們有3種不同的項目類型
        }
    }
}
