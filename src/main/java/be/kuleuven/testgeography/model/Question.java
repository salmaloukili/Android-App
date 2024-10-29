package be.kuleuven.testgeography.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Question implements QuestionInf, Parcelable {
    protected String type;
    protected String descriptionAnswer;// E.g.: If the type is A:Country, then descriptionAnswer could be "Belgium"
    protected String descriptionQuestion;// E.g.: If the type is Q:Capital, then descriptionQuestion could be "Brussels"

    public Question(String typeOfQuestion, String answer, String question){
        type = typeOfQuestion;
        descriptionAnswer = answer;
        descriptionQuestion = question;
    }

    public Question(Parcel in){
        type = in.readString();
        descriptionAnswer = in.readString();
        descriptionQuestion = in.readString();
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    @Override
    public String getType(){
        return type;
    }

    @Override
    public void setType(String type){
        this.type = type;
    }

    @Override
    public String getDescriptionAnswer() {
        return descriptionAnswer;
    }

    @Override
    public void setDescriptionAnswer(String nameAnswer) {
        this.descriptionAnswer = nameAnswer;
    }

    @Override
    public String getDescriptionQuestion() {
        return descriptionQuestion;
    }

    @Override
    public void setDescriptionQuestion(String nameQuestion) {
        this.descriptionQuestion = nameQuestion;
    }

    @Override
    public String makeQuestionString(String q) {
        String str = "";
        /* Normally we would like to do a switch statement */
        switch(type){
            case "Guess the country based on the flag":
                str = "This flag is of which country?";
                break;
            /*case "Guess the flag based on the country":
                str = q + " has which flag?";// q = the country's name
                break;*/
            case "Guess the country based on the capital":
                str = q + " is the capital of ...?";// q = the name of the capital
                break;
            case "Guess the capital based on the country":
                str = "The capital of " + q + " is ...?";// q = the name of the country
                break;
            case "Guess the country based on the abbreviation":
                str = q + " is an abbreviation for ...?";// q = the abbreviation of the country
                break;
            case "Guess the abbreviation based on the country":
                str = "The abbreviation for " + q + " is ...?";// q = the name of the country
                break;
        }
        return str;
    }

    @Override
    public void setupQuestion(ArrayList<String> questionDescriptions, ArrayList<String> answerDescriptions, int questionIndex) {
        setDescriptionAnswer(answerDescriptions.get(questionIndex));
        setDescriptionQuestion(questionDescriptions.get(questionIndex));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(type);
        parcel.writeString(descriptionAnswer);
        parcel.writeString(descriptionQuestion);
    }

    @Override
    public boolean isMCQuestion(){
        return false;
    }
}