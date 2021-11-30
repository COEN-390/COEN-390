package com.coen390.maskdetector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coen390.maskdetector.models.User;

import java.util.ArrayList;
import java.util.List;

public class UsersRecyclerViewAdapter extends RecyclerView.Adapter<UsersRecyclerViewAdapter.ViewHolder> {

    private List<User> users;
    private UsersActivity usersActivity;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView nameText;
        private TextView emailText;
        private TextView userLevelText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameText);
            emailText = itemView.findViewById(R.id.emailText);
            userLevelText = itemView.findViewById(R.id.userLevelText);
        }

        public TextView getNameText() {
            return nameText;
        }
        public TextView getEmailText() {
            return emailText;
        }
        public TextView getUserLevelText() {
            return userLevelText;
        }
    }

    public UsersRecyclerViewAdapter(Context context, UsersActivity usersActivity) {
        this.usersActivity = usersActivity;
        this.users = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getNameText().setText(users.get(position).getName());
        holder.getEmailText().setText(users.get(position).getEmail());
        holder.getUserLevelText().setText(users.get(position).getUserLevel());

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
        return users.size();
    }
    
    public void addUser(User user){
        users.add(user);
    }

    public void deleteUser(User user) {
        for(int i = 0; i < users.size(); i++){
            if(user.get$id().equals(users.get(i).get$id())){
                users.remove(i);
            }
        }
    }

    public void modifyUser(User user) {
        for(int i = 0; i < users.size(); i++){
            if(user.get$id().equals(users.get(i).get$id())){
                users.set(i, user);
            }
        }
    }

    public void setUsersList(List<User> usersList) {
        users = usersList;
    }
}
