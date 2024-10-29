package be.kuleuven.testgeography.model;

public class ItemSpinner {
    private String name;
    private boolean value;
    public ItemSpinner(String name, boolean checked){
        this.name = name;
        this.value = checked;
    }
    public void setCheck(boolean check){
        value = check;
    }
    public void toggleCheck(){
        this.value = !value;
    }
    public void setName(String newName){
        name = newName;
    }
    public String getName(){
        return name;
    }
    public Boolean isChecked(){
        return value;
    }
}
