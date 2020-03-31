package neilsayok.github.nodemcuiotapptest2.SignupLogin.ViewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SignUpViewModel extends ViewModel {

    private MutableLiveData<String> firstName = new MutableLiveData<>();
    private MutableLiveData<String> lastName = new MutableLiveData<>();
    private MutableLiveData<String> email = new MutableLiveData<>();
    private MutableLiveData<String> password = new MutableLiveData<>();



    //Getters
    public LiveData<String> getFirstName() {
        return firstName;
    }

    public LiveData<String> getLastName() {
        return lastName;
    }

    public LiveData<String> getEmail() {
        return email;
    }

    public LiveData<String> getPassword() {
        return password;
    }



    //Setters


    public void setFirstName(String firstName) {
        this.firstName.setValue(firstName);
    }

    public void setLastName(String lastName) {
        this.lastName.setValue(lastName);
    }

    public void setEmail(String email) {
        this.email.setValue(email);
    }

    public void setPassword(String password) {
        this.password.setValue(password);
    }



    public void clearPassword(){
        try {
            password = new MutableLiveData<>();
        }catch (Exception e){
            Log.e("Error","Clearing SignUpViewModel:"+e.getMessage());
        }
    }

    public void clear(){
        try {
            firstName = new MutableLiveData<>();
            lastName = new MutableLiveData<>();
            email = new MutableLiveData<>();
            password = new MutableLiveData<>();
        }catch (Exception e){
            Log.e("Error","Clearing SignUpViewModel:"+e.getMessage());
        }


    }
}
