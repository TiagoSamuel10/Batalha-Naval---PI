package Common;

import java.util.ArrayList;

public class Conversations {

    Conversation[] conversations;

    public Conversations(){
        conversations = new Conversation[3];
        newConversation(0, 0,1);
        newConversation(1, 0,2);
        newConversation(2, 1,2);
    }

    public int getConversationIDWithIDs(int id1, int id2){
        int i = 0;
        for (Conversation conv: conversations) {
            if(conv.isInConv(id1) && conv.isInConv(id2)){
                return i;
            }
            i++;
        }
        System.out.println("NOT THERE");
        return -999;
    }

    public void appendToConversation(int talking, int idConv, String line){
        conversations[idConv].add(talking, line);
    }

    private void newConversation(int id, int id1, int id2){
        conversations[id] = new Conversation(id1, id2);
    }

    public Conversation getConversation(int idc){
        return conversations[idc];
    }

    public Line getLastLineFromConversation(int idc){
        return getConversation(idc).getNewLine();
    }

    public static class Conversation {

        ArrayList <Line> conver;

        private final int id1;
        private final int id2;

        Conversation(int _id1, int _id2) {
            id1 = _id1;
            id2 = _id2;
            conver = new ArrayList<>();
        }

        private boolean isInConv(int id){
            return id == id1 || id == id2;
        }

        private Line[] getAllAsArray(){
            return (Line[])conver.toArray();
        }

        private void add(int talking, String s){
            Line toAdd = new Line(talking, s);
            conver.add(toAdd);
        }

        Line getNewLine(){
            return conver.get(conver.size() - 1);
        }
    }

    public static class Line{
        int id;
        String message;
        public Line(int _id, String _message){
            id = _id;
            message = _message;
        }
        public String decode(String name){
            return name + ": " + message;
        }
    }

}
