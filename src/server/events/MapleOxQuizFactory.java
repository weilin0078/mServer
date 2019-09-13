package server.events;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import database.DatabaseConnection;
import server.Randomizer;
import tools.Pair;

public class MapleOxQuizFactory
{
    private boolean initialized;
    private Map<Pair<Integer, Integer>, MapleOxQuizEntry> questionCache;
    private static MapleOxQuizFactory instance;
    
    public MapleOxQuizFactory() {
        this.initialized = false;
        this.questionCache = new HashMap<Pair<Integer, Integer>, MapleOxQuizEntry>();
    }
    
    public static MapleOxQuizFactory getInstance() {
        return MapleOxQuizFactory.instance;
    }
    
    public boolean hasInitialized() {
        return this.initialized;
    }
    
    public Map.Entry<Pair<Integer, Integer>, MapleOxQuizEntry> grabRandomQuestion() {
        final int size = this.questionCache.size();
        Map.Entry<Pair<Integer, Integer>, MapleOxQuizEntry> oxquiz = null;
    Block_2:
        while (true) {
            final Iterator i$ = this.questionCache.entrySet().iterator();
            while (i$.hasNext()) {
                oxquiz = (Entry<Pair<Integer, Integer>, MapleOxQuizEntry>) i$.next();
                if (Randomizer.nextInt(size) == 0) {
                    break Block_2;
                }
            }
        }
        return oxquiz;
    }
    
    public void initialize() {
        if (this.initialized) {
            return;
        }
        System.out.println("╪сть OX Quiz  :::");
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_oxdata");
            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                this.questionCache.put(new Pair<Integer, Integer>(rs.getInt("questionset"), rs.getInt("questionid")), this.get(rs));
            }
            rs.close();
            ps.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.print("Done\r");
        this.initialized = true;
    }
    
    public MapleOxQuizEntry getFromSQL(final String sql) {
        MapleOxQuizEntry ret = null;
        try {
            final Connection con = DatabaseConnection.getConnection();
            final PreparedStatement ps = con.prepareStatement(sql);
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ret = this.get(rs);
            }
            rs.close();
            ps.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
    
    public static MapleOxQuizEntry getOxEntry(final int questionSet, final int questionId) {
        return getInstance().getOxQuizEntry(new Pair<Integer, Integer>(questionSet, questionId));
    }
    
    public static MapleOxQuizEntry getOxEntry(final Pair<Integer, Integer> pair) {
        return getInstance().getOxQuizEntry(pair);
    }
    
    public MapleOxQuizEntry getOxQuizEntry(final Pair<Integer, Integer> pair) {
        MapleOxQuizEntry mooe = this.questionCache.get(pair);
        if (mooe == null) {
            if (this.initialized) {
                return null;
            }
            mooe = this.getFromSQL("SELECT * FROM wz_oxdata WHERE questionset = " + pair.getLeft() + " AND questionid = " + pair.getRight());
            this.questionCache.put(pair, mooe);
        }
        return mooe;
    }
    
    private MapleOxQuizEntry get(final ResultSet rs) throws SQLException {
        return new MapleOxQuizEntry(rs.getString("question"), rs.getString("display"), this.getAnswerByText(rs.getString("answer")), rs.getInt("questionset"), rs.getInt("questionid"));
    }
    
    private int getAnswerByText(final String text) {
        if (text.equalsIgnoreCase("x")) {
            return 0;
        }
        if (text.equalsIgnoreCase("o")) {
            return 1;
        }
        return -1;
    }
    
    static {
        MapleOxQuizFactory.instance = new MapleOxQuizFactory();
    }
    
    public static class MapleOxQuizEntry
    {
        private String question;
        private String answerText;
        private int answer;
        private int questionset;
        private int questionid;
        
        public MapleOxQuizEntry(final String question, final String answerText, final int answer, final int questionset, final int questionid) {
            this.question = question;
            this.answerText = answerText;
            this.answer = answer;
            this.questionset = questionset;
            this.questionid = questionid;
        }
        
        public String getQuestion() {
            return this.question;
        }
        
        public String getAnswerText() {
            return this.answerText;
        }
        
        public int getAnswer() {
            return this.answer;
        }
        
        public int getQuestionSet() {
            return this.questionset;
        }
        
        public int getQuestionId() {
            return this.questionid;
        }
    }
}
