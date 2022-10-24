package com.doctor.doctorsappointment.PatientFragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.doctor.doctorsappointment.Adapters.DoctorAdatper;
import com.doctor.doctorsappointment.DataRetrievalClass.Doctor;
import com.doctor.doctorsappointment.ReusableFunctionsAndObjects;
import java.util.ArrayList;
import java.util.List;

import doctor.doctorsappointment.R;

public class PatientSearchDoctorsFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private List<Doctor> doctors;
    private DoctorAdatper doctorAdatper;
    private SearchView searchView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_common,container,false);
        setHasOptionsMenu(true);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        doctors= new ArrayList<>();
        progressDialog= new ProgressDialog(getContext());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...\nPlease wait...");
        progressDialog.show();


//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserDetails").child(
//                FirebaseAuth.getInstance().getCurrentUser().getUid());
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String type = snapshot.child("UserType").getValue().toString();
//                if (type.equals("Patient")){
//                    readPatients();
//                }
//                else {
//                    readPatients();
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//
//        });
//

        FirebaseDatabase.getInstance().getReference().child("UserDetails").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                doctors.clear();
                for(DataSnapshot childsnapshot:snapshot.getChildren()){
                    doctors.add(childsnapshot.getValue(Doctor.class));
                }
                doctorAdatper=new DoctorAdatper(getContext(),doctors,getActivity());
                recyclerView.setAdapter(doctorAdatper);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                ReusableFunctionsAndObjects.showMessageAlert(getContext(),"Network Error",error.getMessage(),"Ok",(byte)0);
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.my_search_menu,menu);
        MenuItem menuItem=menu.findItem(R.id.search_bar);
        searchView=(SearchView)menuItem.getActionView();
        searchView.setQueryHint("Search Doctor/Specialization");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query!=null){
                    filter(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText!=null){
                    filter(newText);
                }
                return true;
            }
        });
    }

    private void filter(String s){
        List<Doctor> filteredlist=new ArrayList<>();
        for(Doctor doctor: doctors){
            if(doctor.getFirstName().toLowerCase().contains(s.toLowerCase())||doctor.getLastName().toLowerCase().contains(s.toLowerCase())||doctor.getSpecialization().toLowerCase().contains(s.toLowerCase())){
                filteredlist.add(doctor);
            }
        }
        doctorAdatper=new DoctorAdatper(getContext(),filteredlist,getActivity());
        recyclerView.setAdapter(doctorAdatper);
    }


//
//    private void readPatients() {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
//                .child("UserDetails");
//        Query query = reference.orderByChild("UserType").equalTo("Doctor");
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                doctors.clear();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    Doctor user = snapshot.getValue(Doctor.class);
//                    doctors.add(user);
//                }
//                doctorAdatper.notifyDataSetChanged();
//                progressDialog.dismiss();
//                if (doctors.isEmpty()){
//
//                    Toast.makeText(getContext(), "No Patient found", Toast.LENGTH_SHORT).show();
//                    progressDialog.dismiss();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });
//    }


}