package com.example.daily_report;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

public class EveningRoutineFragment extends Fragment {


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
        Log.e("123", "Evening onCreateView 함수 호출");

        return inflater.inflate(R.layout.fragment_evening_routine, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        updateCheck="normal";


        routineRecyclerview = view.findViewById(R.id.routine_recyclerview);
        linearLayoutManager = new LinearLayoutManager(view.getContext());

        routineDataList = new ArrayList<DailyRoutineData>();
        dailyRoutineAdapter = new DailyRoutineAdapter(routineDataList);

        routineRecyclerview.setAdapter(dailyRoutineAdapter);
        routineRecyclerview.setLayoutManager(linearLayoutManager);

        //저장된 arrayList를 불러오고 빈깡통일 경우, 전체루틴으로부터 기본세팅을 해준다.
        setRecyclerView();

        ((DailyRoutineActivity)getActivity()).setHorizontalEveningListener(new DailyRoutineActivity.HorizontalCalenderSelectedListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {

                //날짜 바뀌는 타이밍에 함수 재사용하여, 따로 함수 빼서 사용.

                setRecyclerView();

            }

            @Override
            public boolean isMorning() {
                return false;
            }
        });


        //수정 런쳐 사용
        updateLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if (result.getResultCode() == 101) {


                            //update 관리 어떻게 할 것인가..
                            //인텐트에, 기존 그날의 리사이클러뷰 만 수정할 것인지 오늘+앞으로의 루틴에 적용할 것인지.
                            Intent intent = result.getData();
                            Bundle bundle = intent.getExtras();
                            int position = bundle.getInt("position");
                            int serialNumber= bundle.getInt("serialNumber");

                            updateCheck = bundle.getString("update");

                            DailyRoutineData routineData = new DailyRoutineData(bundle.getString("routineTime"), bundle.getString("routineName"), bundle.getString("routineType"), bundle.getString("routineRepeat"), false,serialNumber);
                            //수정 detail한 부분
                            String updateKind=bundle.getString("change");

                            //해당 날짜만 변경하는 경우
                            if(updateKind.equals("changeOnlyThisDay")){

                                Log.e("123","당일만 수정 ");
                                routineDataList.set(position, routineData);
                                dailyRoutineAdapter.notifyItemChanged(position);
                                MySharedPreference.setRoutineArrayList(getActivity(),MainActivity.dateControl,routineDataList,"저녁루틴");


                            }
                            //해당 날짜와 앞으로의 루틴을 수정하는 경우
                            else if(updateKind.equals("changeAllDay")){
                                Log.e("123","모든날 수정 ");

                                //앞으로의 날짜에 추가하기 위해선, 전체 루틴에서 해당 항목을 수정해야한다.
                                ArrayList<DailyRoutineData> totalArrayList = new ArrayList<DailyRoutineData>();
                                totalArrayList=MySharedPreference.getRoutineArrayList(getActivity(),"나의루틴","전체루틴");

                                for(int i=0;i<totalArrayList.size();i++){
                                    if(serialNumber==totalArrayList.get(i).getSerialNumber()){

                                        totalArrayList.set(i,routineData);
                                        break;
                                    }
                                }
                                MySharedPreference.setRoutineArrayList(getActivity(),"나의루틴",totalArrayList,"전체루틴");


                                //해당 날짜의 루틴을 수정할 때에는, 일단 수정된 내용에서 루틴주기가 해당날짜를 포함하고 있는지 확인하고 존재하면 내용 수정.
                                String getDay = MainActivity.dateControl.substring(MainActivity.dateControl.length()-1);
                                if(routineData.getRoutineRepeat().contains(getDay)){

                                    routineDataList.set(position, routineData);
                                    dailyRoutineAdapter.notifyItemChanged(position);
                                    MySharedPreference.setRoutineArrayList(getActivity(),MainActivity.dateControl,routineDataList,"저녁루틴");
                                }
                                //해당 날짜의 루틴 수정할 때, 수정된 내용에서 루틴주기(월,화,수)가 현재 선택 날짜의 요일을 갖고 있지 않다면, routineDataList에서 삭제해야한다.
                                else{
                                    for(int i =0;i<routineDataList.size();i++){

                                        if(routineData.getSerialNumber()==routineDataList.get(i).getSerialNumber()){
                                            routineDataList.remove(i);
                                            dailyRoutineAdapter.notifyItemRemoved(i);
                                            MySharedPreference.setRoutineArrayList(getActivity(),MainActivity.dateControl,routineDataList,"저녁루틴");
                                            break;

                                        }
                                    }


                                }


                            }



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
                bundle.putInt("serialNumber",item.getSerialNumber());
                i.putExtras(bundle);
                updateLauncher.launch(i);
            }

            @Override
            public void onItemLongClick(DailyRoutineAdapter.DailyRoutineViewHolder dailyRoutineViewHolder, View itemView, int position) {

      /*          AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getActivity());
                deleteDialog.setMessage("이 기록을 삭제하시겠습니까?");
                deleteDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        routineDataList.remove(position);
                        dailyRoutineAdapter.notifyItemRemoved(position);
                        MySharedPreference.setRoutineArrayList(getActivity(),MainActivity.dateControl,routineDataList,"저녁기록");
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
*/


                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View dialogView =LayoutInflater.from(getActivity()).inflate(R.layout.dialog_routine_update_choose,null,false);
                TextView firstChoice=dialogView.findViewById(R.id.first_choice);
                TextView secondChoice=dialogView.findViewById(R.id.second_choice);

                firstChoice.setText("오늘루틴에서만 삭제하기");
                secondChoice.setText("앞으로 루틴에서 모두 삭제하기");
                builder.setView(dialogView);
                AlertDialog chooseDialog=builder.create();

                chooseDialog.show();


                //오늘 루틴에서만 제거할 경우
                firstChoice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        routineDataList.remove(position);
                        dailyRoutineAdapter.notifyItemRemoved(position);
                        MySharedPreference.setRoutineArrayList(getActivity(), MainActivity.dateControl, routineDataList, "저녁루틴");

                        chooseDialog.dismiss();



                    }
                });
                //앞으로의 모든 루틴에서 다 제거할 경우
                secondChoice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        ArrayList<DailyRoutineData> totalArrayList=MySharedPreference.getRoutineArrayList(getActivity(),"나의루틴","전체루틴");
                        for(int i=0;i<totalArrayList.size();i++){
                            if(routineDataList.get(position).getSerialNumber()==totalArrayList.get(i).getSerialNumber()){
                                totalArrayList.remove(i);
                                MySharedPreference.setRoutineArrayList(getActivity(),"나의루틴",totalArrayList,"전체루틴");
                                break;
                            }
                        }

                        routineDataList.remove(position);
                        dailyRoutineAdapter.notifyItemRemoved(position);
                        MySharedPreference.setRoutineArrayList(getActivity(), MainActivity.dateControl, routineDataList, "저녁루틴");

                        chooseDialog.dismiss();

                    }
                });




            }
        });

    }

    public void setRecyclerView(){
        //날짜 바뀌는 타이밍에 리사이클러뷰 저장된걸 가져오던지 OR NO VALUE 이면 전체 루틴으로부터 세팅해주던지
        routineDataList=MySharedPreference.getRoutineArrayList(getActivity(),MainActivity.dateControl,"저녁기록");
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

                if(getRoutineRepeat.contains(getDay)&&getRoutineType.equals("저녁")){
                    routineDataList.add(allArrayList.get(i));
                }
            }

            dailyRoutineAdapter.setRoutineDataList(routineDataList);
            dailyRoutineAdapter.notifyDataSetChanged();
            MySharedPreference.setRoutineArrayList(getActivity(),MainActivity.dateControl,routineDataList,"저녁기록");

        }
        else{
            dailyRoutineAdapter.setRoutineDataList(routineDataList);
            dailyRoutineAdapter.notifyDataSetChanged();
        }

    }




    @Override
    public void onStart() {
        super.onStart();
        Log.e("123", "Evening onStart() 함수 호출");
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
        if (updateCheck.equals("update")) {
            Log.e("123","신규수정중입니다.");
            updateCheck="noraml";
        } else {

            if (bundle != null) {
                Log.e("123", "신규추가입니다.");

/*
                totalArrayList = MySharedPreference.getRoutineArrayList(getActivity(), "나의루틴", "전체루틴");
                //여기서 번들바꿔서 리사이클러뷰 정보 바꿔주기.

                // 전체루틴에는 무조건 추가가 된다. 그 때, 일련번호를 삽입하여서, 나중에 추가 ,삭제할 때 사용하자.
                int serialNumber;
                int randomNum;
                while(true){
                    boolean check=false;
                    randomNum=(int)(Math.random()*1000+1);
                    for(int i=0; i<totalArrayList.size();i++){
                        if(randomNum==totalArrayList.get(i).getSerialNumber()){
                            check=true;
                            break;
                        }
                    }
                    if(check==false){
                        break;
                    }
                }

                serialNumber=randomNum;
                Log.e("123","시리얼 넘버 : "+ serialNumber);

*/

                ArrayList<DailyRoutineData> tempArrayList = new ArrayList<DailyRoutineData>();

                totalArrayList = MySharedPreference.getRoutineArrayList(getActivity(), "나의루틴", "전체루틴");
                String getDay = MainActivity.dateControl.substring(MainActivity.dateControl.length() - 1);

                // 이 부분에서 날짜를 체크해서 넘겨야 기존 recyclerView에 찍을지 말지 결정
                DailyRoutineData routineData = new DailyRoutineData(bundle.getString("routineTime"), bundle.getString("routineName"), bundle.getString("routineType"), bundle.getString("routineRepeat"), false, bundle.getInt("serialNumber"));


                if (routineDataList.isEmpty()) {
                    Log.e("123", "routineDataList 비어 있습니다. ");

                    //여기서 전체루틴 가져와서 날짜 비교해서 뿌려주고 data 저장해야지
                    ArrayList<DailyRoutineData> allArrayList = MySharedPreference.getRoutineArrayList(getActivity(), "나의루틴", "전체루틴");
                    //Log.e("123", "allArrayList : "+allArrayList);

                    for (int i = 0; i < allArrayList.size(); i++) {

                        String getRoutineRepeat = allArrayList.get(i).getRoutineRepeat();
                        String getRoutineType = allArrayList.get(i).getRoutineType();
                        if (getRoutineRepeat.contains(getDay) && getRoutineType.equals("저녁")) {
                            routineDataList.add(allArrayList.get(i));
                        }
                    }

                    dailyRoutineAdapter.setRoutineDataList(routineDataList);
                    dailyRoutineAdapter.notifyDataSetChanged();
                    MySharedPreference.setRoutineArrayList(getActivity(), MainActivity.dateControl, routineDataList, "저녁루틴");

                }
                else{
                    Log.e("123","여기로 들어오긴 하냐 11");
                    for (int i = 0; i < totalArrayList.size(); i++) {
                        if(totalArrayList.get(i).getRoutineType().equals("저녁")&&totalArrayList.get(i).getRoutineRepeat().contains(getDay)){
                            tempArrayList.add(totalArrayList.get(i));
                        }
                    }
                    Log.e("123","여기로 들어오긴 하냐 22");
                    for(int i=0;i<tempArrayList.size();i++){
                        Log.e("123","tempArrayList : " + tempArrayList.get(i).getSerialNumber());
                    }
                    for(int i=0;i<totalArrayList.size();i++){
                        Log.e("123","totalArrayList : " + totalArrayList.get(i).getSerialNumber());
                    }


                    boolean check;
                    for(int i=0; i<tempArrayList.size();i++){
                        check=false;
                        for(int j=0; j<routineDataList.size();j++){
                            if(tempArrayList.get(i).getSerialNumber()==routineDataList.get(j).getSerialNumber()){
                                check=true;
                            }

                        }
                        if(!check){
                            Log.e("123","여기로 들어오긴 하냐 33");
                            routineDataList.add(tempArrayList.get(i));
                            dailyRoutineAdapter.notifyItemInserted(routineDataList.size() - 1);
                            MySharedPreference.setRoutineArrayList(getActivity(), MainActivity.dateControl, routineDataList, "저녁루틴");
                        }


                    }

                }



                //날짜 비교해서 현재 날짜 요일과 같으면 recycelrView에 띄어주면서 동시에, 저장


                //루틴 추가할 떄, 전체루틴에 항상 추가하고, + 해당 recyclerView에 날짜가 동일하면, 날짜별, recyclerView에 추가
          /*
                totalArrayList.add(routineData);
                MySharedPreference.setRoutineArrayList(getActivity(), "나의루틴", totalArrayList, "전체루틴");

*/

                resumeCheck = true;
                bundle = null;

            }

        }


        Log.e("123", "evening onResume() 함수 호출");

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.e("123", "Evening onDestroyView() 함수 호출");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("123", "Evening onDestroy() 함수 호출");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e("123", "Evening onDetach() 함수 호출");
    }

    @Override
    public void onPause() {
        Log.e("123", "Evening onPause() 함수 호출");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.e("123", "Evening onStop() 함수 호출");
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.e("123", "Evening onSaveInstanceState() 함수 호출");
        super.onSaveInstanceState(outState);
    }
}
