package com.example.daily_report;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

public class MorningRoutineFragment extends Fragment {

    RecyclerView routineRecyclerview;
    DailyRoutineAdapter dailyRoutineAdapter;
    LinearLayoutManager linearLayoutManager;
    ArrayList<DailyRoutineData> routineDataList;
    ArrayList<DailyRoutineData> totalArrayList;

    private Bundle bundle;
    private boolean resumeCheck = false;
    private String updateCheck;
    private ActivityResultLauncher<Intent> updateLauncher;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("123", "morning onCreateView 함수 호출");


        return inflater.inflate(R.layout.fragment_moring_routine, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateCheck = "normal";
        routineRecyclerview = view.findViewById(R.id.routine_recyclerview);
        linearLayoutManager = new LinearLayoutManager(view.getContext());

        routineDataList = new ArrayList<DailyRoutineData>();
        dailyRoutineAdapter = new DailyRoutineAdapter(routineDataList);

        routineRecyclerview.setAdapter(dailyRoutineAdapter);
        routineRecyclerview.setLayoutManager(linearLayoutManager);

        //Fragment 화면 맨처음 생겼을 때,
        //저장된 arrayList를 불러오고 없으면 세팅해주는 시스템이 필요하다.

        setRecyclerView();


        ((DailyRoutineActivity) getActivity()).setHorizontalMorningListener(new DailyRoutineActivity.HorizontalCalenderSelectedListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {

                //함수 재사용을위하여, 따로 함수 뺴서 사용.
                setRecyclerView();

            }

            @Override
            public boolean isMorning() {
                return true;
            }
        });


        //수정 런쳐 사용
        updateLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if (result.getResultCode() == 100) {

                            Intent intent = result.getData();
                            Bundle bundle = intent.getExtras();
                            int position = bundle.getInt("position");
                            updateCheck = bundle.getString("update");
                            DailyRoutineData routineData = new DailyRoutineData(bundle.getString("routineTime"), bundle.getString("routineName"), bundle.getString("routineType"), bundle.getString("routineRepeat"), false);

                            routineDataList.set(position, routineData);
                            dailyRoutineAdapter.notifyItemChanged(position);
                            MySharedPreference.setRoutineArrayList(getActivity(),MainActivity.dateControl,routineDataList,"아침루틴");

                        }

                    }
                }
        );


        //수정하기
        dailyRoutineAdapter.setOnclickListener(new DailyRoutineAdapter.OnRoutineItemClickListener() {
            @Override
            public void setItemClick(DailyRoutineAdapter.DailyRoutineViewHolder dailyRoutineViewHolder, View itemView, int position) {

                DailyRoutineData item = routineDataList.get(position);

                //인텐트 보내면서, 수정할 내용들도 같이 보내는 작업 TODO 화면에 보이지 않는 정보들도 받아오고 보내고 이거 추가해야함
                Intent i = new Intent(getActivity(), DailyRoutinePlusActivity.class);
                Bundle bundle = new Bundle();

                bundle.putString("routineName", item.getRoutineContent());
                bundle.putString("routineTime", item.getRoutineTime());
                bundle.putString("routineType", item.getRoutineType());
                bundle.putString("routineRepeat", item.getRoutineRepeat());
                bundle.putInt("position", position);
                i.putExtras(bundle);
                updateLauncher.launch(i);
            }

            @Override
            public void onItemLongClick(DailyRoutineAdapter.DailyRoutineViewHolder dailyRoutineViewHolder, View itemView, int position) {

                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getActivity());
                deleteDialog.setMessage("이 기록을 삭제하시겠습니까?");
                deleteDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        routineDataList.remove(position);
                        dailyRoutineAdapter.notifyItemRemoved(position);
                        MySharedPreference.setRoutineArrayList(getActivity(),MainActivity.dateControl,routineDataList,"아침루틴");
                        dialogInterface.dismiss();


                    }
                });
                deleteDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                });

                deleteDialog.show();

            }
        });


    }

    public void setRecyclerView(){
        //날짜 바뀌는 타이밍에 리사이클러뷰 저장된걸 가져오던지 OR NO VALUE 이면 전체 루틴으로부터 세팅해주던지
        routineDataList=MySharedPreference.getRoutineArrayList(getActivity(),MainActivity.dateControl,"아침루틴");
        Log.e("123","routineDataList : " + routineDataList);


        //기록된 것이 아무것도 없을 떄, 전체기록으로부터, 요일 필터링을 걸쳐서 세팅해주는 작업을 해야한다.
        if(routineDataList.isEmpty()){
            Log.e("123","routineDataList 비어 있습니다. ");
            String getDay =MainActivity.dateControl.substring(MainActivity.dateControl.length()-1);

            //여기서 전체루틴 가져와서 날짜 비교해서 뿌려주고 data 저장해야지
            ArrayList<DailyRoutineData> allArrayList =MySharedPreference.getRoutineArrayList(getActivity(),"나의루틴","전체루틴");
            //Log.e("123", "allArrayList : "+allArrayList);

            for(int i=0;i<allArrayList.size();i++){
                String getRoutineRepeat =allArrayList.get(i).getRoutineRepeat();
                String getRoutineType= allArrayList.get(i).getRoutineType();
                if(getRoutineRepeat.contains(getDay)&&getRoutineType.equals("아침")){
                    routineDataList.add(allArrayList.get(i));
                }
            }

            dailyRoutineAdapter.setRoutineDataList(routineDataList);
            dailyRoutineAdapter.notifyDataSetChanged();
            MySharedPreference.setRoutineArrayList(getActivity(),MainActivity.dateControl,routineDataList,"아침루틴");

        }
        else{
            dailyRoutineAdapter.setRoutineDataList(routineDataList);
            dailyRoutineAdapter.notifyDataSetChanged();
        }

    }


    @Override
    public void onStart() {
        super.onStart();
        Log.e("123", "morning onStart() 함수 호출");

        resumeCheck = false;
    }

    //추가하기.
    @Override
    public void onResume() {
        super.onResume();

        if (!resumeCheck) {

            bundle = this.getArguments();
            Bundle newBundle = new Bundle();
            newBundle = null;
            this.setArguments(newBundle);

        }
        if (updateCheck.equals("update")) {
            updateCheck = "noraml";
        } else {

            if (bundle != null) {
                Log.e("123","화면 열 때, 이거 사용하는지 확인");

                totalArrayList = MySharedPreference.getRoutineArrayList(getActivity(), "나의루틴", "전체루틴");
                //여기서 번들바꿔서 리사이클러뷰 정보 바꿔주기.

                // 이 부분에서 날짜를 체크해서 넘겨야 기존 recyclerView에 찍을지 말지 결정
                DailyRoutineData routineData = new DailyRoutineData(bundle.getString("routineTime"), bundle.getString("routineName"), bundle.getString("routineType"), bundle.getString("routineRepeat"), false);

                //날짜 비교해서 현재 날짜 요일과 같으면 recycelrView에 띄어주면서 동시에, 저장
                String getDay =MainActivity.dateControl.substring(MainActivity.dateControl.length()-1);

                if(bundle.getString("routineRepeat").contains(getDay)&&bundle.getString("routineType").equals("아침") ){
                    routineDataList.add(routineDataList.size(), routineData);
                    dailyRoutineAdapter.notifyItemInserted(routineDataList.size() - 1);
                    MySharedPreference.setRoutineArrayList(getActivity(), MainActivity.dateControl, routineDataList, "아침루틴");
                }

                //루틴 추가할 떄, 전체루틴에 항상 추가하고, + 해당 recyclerView에 날짜가 동일하면, 날짜별, recyclerView에 추가
                totalArrayList.add(routineData);
                MySharedPreference.setRoutineArrayList(getActivity(), "나의루틴", totalArrayList, "전체루틴");
                resumeCheck = true;
                bundle = null;


            }
        }

        Log.e("123", "morning onResume() 함수 호출");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.e("123", "morning onDestroyView() 함수 호출");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("123", "morning onDestroy() 함수 호출");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e("123", "morning onDetach() 함수 호출");
    }

    @Override
    public void onPause() {
        Log.e("123", "morning onPause() 함수 호출");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.e("123", "morning onStop() 함수 호출");
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.e("123", "morning onSaveInstanceState() 함수 호출");
        super.onSaveInstanceState(outState);
    }
}
