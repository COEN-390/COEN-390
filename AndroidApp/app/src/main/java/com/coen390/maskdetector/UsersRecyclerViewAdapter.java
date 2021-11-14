package com.coen390.maskdetector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coen390.maskdetector.controllers.SharedPreferencesHelper;

import java.util.List;

public class UsersRecyclerViewAdapter extends RecyclerView.Adapter<UsersRecyclerViewAdapter.ViewHolder> {

    private SharedPreferencesHelper sharedPreferencesHelper;
    private List<String> users;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView nameText;
        private TextView emailText;
        private TextView passwordText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameText);
            emailText = itemView.findViewById(R.id.eventText);
            passwordText = itemView.findViewById(R.id.passwordText);
        }

        public TextView getNameText() {
            return nameText;
        }
        public TextView getEmailText() {
            return emailText;
        }
        public TextView getPasswordText() {
            return passwordText;
        }
    }

    public UsersRecyclerViewAdapter(Context context, List<String> users) {
        this.sharedPreferencesHelper = new SharedPreferencesHelper(context);
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getNameText().setText("Name");
        holder.getEmailText().setText("Email");
        holder.getPasswordText().setText("Password");

        // TODO: add a DF on item click that prompts for user deletion or elements change
        // Set onClickListener for every item to the same activity
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int pos = holder.getLayoutPosition();
//
//                Intent intent = new Intent(view.getContext(), ProfileActivity.class);
//                intent.putExtra("studentId", sortedProfiles.get(pos).getStudentId());
//                view.getContext().startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        if(users.equals("")) return 0;
        else return users.size();
    }
}
