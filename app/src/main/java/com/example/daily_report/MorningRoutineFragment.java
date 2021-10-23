package com.example.daily_report;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class MorningRoutineFragment extends Fragment {

    RecyclerView routineRecyclerview;
    DailyRoutineAdapter dailyRoutineAdapter;
    LinearLayoutManager linearLayoutManager;
    ArrayList<DailyRoutineData> routineDataList;
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

        routineDataList.add(new DailyRoutineData("기상직후", "이불개기", "아침", "월 화 수 목 금 토 일 ", false));
        routineDataList.add(new DailyRoutineData("기상직후", "물마시기", "아침", "월 화 수 목 금 토 일 ", false));
        routineDataList.add(new DailyRoutineData("기상직후", "명상하기", "아침", "월 화 수 목 금 토 일 ", false));
        routineDataList.add(new DailyRoutineData("기상직후", "운동5분", "아침", "월 화 수 목 금 토 일 ", false));
        dailyRoutineAdapter.notifyDataSetChanged();


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

    @Override
    public void onStart() {
        super.onStart();
        Log.e("123", "morning onStart() 함수 호출");

        resumeCheck = false;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!resumeCheck) {

            bundle = this.getArguments();
            Bundle newBundle = new Bundle();
            newBundle = null;
            this.setArguments(newBundle);

        }
        Log.e("123", "bundle로 막기 : " + bundle + "resumeCheck : " + resumeCheck);
        if (updateCheck.equals("update")) {
            updateCheck = "noraml";
        } else {

            if (bundle != null) {

                //여기서 번들바꿔서 리사이클러뷰 정보 바꿔주기.

                DailyRoutineData routineData = new DailyRoutineData(bundle.getString("routineTime"), bundle.getString("routineName"), bundle.getString("routineType"), bundle.getString("routineRepeat"), false);

                if (routineDataList.size() == 0) {
                    routineDataList.add(routineDataList.size(), routineData);
                    dailyRoutineAdapter.notifyItemInserted(routineDataList.size() - 1);
                } else if (routineData.getRoutineTime().equals(routineDataList.get(routineDataList.size() - 1).getRoutineTime()) && routineData.getRoutineContent().equals(routineDataList.get(routineDataList.size() - 1).getRoutineContent())) {

                } else {
                    routineDataList.add(routineDataList.size(), routineData);
                    dailyRoutineAdapter.notifyItemInserted(routineDataList.size() - 1);
                }
                resumeCheck = true;
                bundle = null;
                Log.e("123", "bundle로  : " + bundle + "resumeCheck : " + resumeCheck);

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
