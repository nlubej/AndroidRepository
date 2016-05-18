package nlubej.gains.Database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;

import nlubej.gains.DataTransferObjects.ExerciseDto;
import nlubej.gains.DataTransferObjects.ExerciseType;
import nlubej.gains.DataTransferObjects.ProgramDto;
import nlubej.gains.DataTransferObjects.RoutineDto;
import nlubej.gains.Database.Queries.ExerciseQueries;
import nlubej.gains.Database.Queries.ProgramQueries;
import nlubej.gains.Database.Queries.RoutineQueries;


public class QueryFactory
{

    private static final int DatabaseVersion = 1;
    private static String DatabaseName = "GainsDb";
    //Database creation sql statement
    final String CREATE_TABLE_PROGRAM = ProgramQueries.CreateTableProgram();
    private static final String CREATE_TABLE_EXERCISE = ExerciseQueries.CreateTableExercise();
    private static final String CREATE_TABLE_ROUTINE_EXERCISE = ExerciseQueries.CreateTableRoutineExercise();
    private static final String CREATE_TABLE_EXERCISE_TYPE = ExerciseQueries.CreateTableExerciseType();
    private static final String CREATE_TABLE_ROUTINE = RoutineQueries.CreateTableRoutine();
    private static final String CREATE_TABLE_LOGGED_WORKOUT = ExerciseQueries.CreateTableLoggedWorkout();
    private static final String CREATE_TABLE_TMP_LOGGED_WORKOUT = ExerciseQueries.CreateTableTmpLoggedWorkout();
    private static final String CREATE_TABLE_USER = "create table if not exists USER (USER_ID integer primary key autoincrement, USER_WEIGHT double, USER_HEIGHT double);";
    private static final String CREATE_TABLE_WORKOUT_NOTE = ExerciseQueries.CreateTableWorkoutNote();
    private static final String CREATE_TABLE_TUTORIAL = "create table if not exists USER (TUTORIAL_ID integer primary key autoincrement, NAME varchar not null, TUTORIAL_SEEN integer not null default 0 );";

    //private static final String CREATE_TABLE_ACHIEVEMENT_LOG = "create table if not exists achievementLog (id integer primary key autoincrement,  name VARCHAR not null, value integer not null);";

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private final Context context;

    public class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DatabaseName, null, DatabaseVersion);
        }


        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try
            {
                createDb(db);
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }

        public void createDb(SQLiteDatabase db)
        {
            db.execSQL(CREATE_TABLE_PROGRAM);
            db.execSQL(CREATE_TABLE_ROUTINE);
            db.execSQL(CREATE_TABLE_ROUTINE_EXERCISE);
            db.execSQL(CREATE_TABLE_EXERCISE);
            db.execSQL(CREATE_TABLE_EXERCISE_TYPE);
            db.execSQL(CREATE_TABLE_LOGGED_WORKOUT);
            db.execSQL(CREATE_TABLE_TMP_LOGGED_WORKOUT);
            db.execSQL(CREATE_TABLE_USER);
            db.execSQL(CREATE_TABLE_WORKOUT_NOTE);
            db.execSQL(CREATE_TABLE_TUTORIAL);


            db.execSQL(String.format("INSERT INTO EXERCISE_TYPE (EXERCISE_TYPE_ID, DESCRIPTION) VALUES (%s, '%s')",1,"Chest"));
            db.execSQL(String.format("INSERT INTO EXERCISE_TYPE (EXERCISE_TYPE_ID, DESCRIPTION) VALUES (%s, '%s')",2,"Arms"));
            db.execSQL(String.format("INSERT INTO EXERCISE_TYPE (EXERCISE_TYPE_ID, DESCRIPTION) VALUES (%s, '%s')",3,"Legs"));

            InsertAll(db);
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w("ZZZZZZ", "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            String upgradeQuery = "ALTER TABLE log ADD COLUMN comment TEXT";
            if (oldVersion == 1 && newVersion == 2)
            {
                return; //.execSQL(CREATE_TABLE_NOTE);
            }
        }
    }

    private void InsertAll(SQLiteDatabase db)
    {
        db.execSQL(String.format("INSERT INTO PROGRAM (PROGRAM_ID, PROGRAM_NAME) VALUES (%s, '%s')", 1, "Gains"));


        db.execSQL(String.format("INSERT INTO ROUTINE (ROUTINE_ID, ROUTINE_POS,ROUTINE_NAME,PROGRAM_ID) VALUES (%s, %s, '%s', %s)", 1, 1, "Leg Day", 1));
        db.execSQL(String.format("INSERT INTO ROUTINE (ROUTINE_ID, ROUTINE_POS,ROUTINE_NAME,PROGRAM_ID) VALUES (%s, %s, '%s', %s)", 2, 2, "Chest/Triceps", 1));


        db.execSQL(String.format("INSERT INTO EXERCISE (EXERCISE_ID, EXERCISE_NAME, EXERCISE_TYPE) VALUES (%s, '%s', %s)", 1, "Squats", 3));
        db.execSQL(String.format("INSERT INTO EXERCISE (EXERCISE_ID, EXERCISE_NAME, EXERCISE_TYPE) VALUES (%s, '%s', %s)", 2, "Lunges", 3));
        db.execSQL(String.format("INSERT INTO EXERCISE (EXERCISE_ID, EXERCISE_NAME, EXERCISE_TYPE) VALUES (%s, '%s', %s)", 3, "One Leged Leg Press", 3));
        db.execSQL(String.format("INSERT INTO EXERCISE (EXERCISE_ID, EXERCISE_NAME, EXERCISE_TYPE) VALUES (%s, '%s', %s)", 4, "Leg Extensions", 3));


        db.execSQL(String.format("INSERT INTO ROUTINE_EXERCISE (ROUTINE_EXERCISE_ID, ROUTINE_ID, EXERCISE_ID, EXERCISE_POS) VALUES (%s, %s, %s, %s)", 1, 1, 1, 1));
        db.execSQL(String.format("INSERT INTO ROUTINE_EXERCISE (ROUTINE_EXERCISE_ID, ROUTINE_ID, EXERCISE_ID, EXERCISE_POS) VALUES (%s, %s, %s, %s)", 2, 1, 2, 2));
        db.execSQL(String.format("INSERT INTO ROUTINE_EXERCISE (ROUTINE_EXERCISE_ID, ROUTINE_ID, EXERCISE_ID, EXERCISE_POS) VALUES (%s, %s, %s, %s)", 3, 1, 3, 3));
        db.execSQL(String.format("INSERT INTO ROUTINE_EXERCISE (ROUTINE_EXERCISE_ID, ROUTINE_ID, EXERCISE_ID, EXERCISE_POS) VALUES (%s, %s, %s, %s)", 4, 1, 4, 4));


        db.execSQL(String.format("INSERT INTO EXERCISE (EXERCISE_ID, EXERCISE_NAME, EXERCISE_TYPE) VALUES (%s, '%s', %s)", 5, "Bench Press", 1));
        db.execSQL(String.format("INSERT INTO EXERCISE (EXERCISE_ID, EXERCISE_NAME, EXERCISE_TYPE) VALUES (%s, '%s', %s)", 6, "Dumbbell Press", 1));
        db.execSQL(String.format("INSERT INTO EXERCISE (EXERCISE_ID, EXERCISE_NAME, EXERCISE_TYPE) VALUES (%s, '%s', %s)", 7, "Cable Flies", 1));
        db.execSQL(String.format("INSERT INTO EXERCISE (EXERCISE_ID, EXERCISE_NAME, EXERCISE_TYPE) VALUES (%s, '%s', %s)", 8, "Machine Press", 1));


        db.execSQL(String.format("INSERT INTO ROUTINE_EXERCISE (ROUTINE_EXERCISE_ID, ROUTINE_ID, EXERCISE_ID, EXERCISE_POS) VALUES (%s, %s, %s, %s)", 5, 2, 5, 1));
        db.execSQL(String.format("INSERT INTO ROUTINE_EXERCISE (ROUTINE_EXERCISE_ID, ROUTINE_ID, EXERCISE_ID, EXERCISE_POS) VALUES (%s, %s, %s, %s)", 6, 2, 6, 2));
        db.execSQL(String.format("INSERT INTO ROUTINE_EXERCISE (ROUTINE_EXERCISE_ID, ROUTINE_ID, EXERCISE_ID, EXERCISE_POS) VALUES (%s, %s, %s, %s)", 7, 2, 7, 3));
        db.execSQL(String.format("INSERT INTO ROUTINE_EXERCISE (ROUTINE_EXERCISE_ID, ROUTINE_ID, EXERCISE_ID, EXERCISE_POS) VALUES (%s, %s, %s, %s)", 8, 2, 8, 4));
    }

    public QueryFactory(Context ctx)
    {
        this.context = ctx;
        dbHelper = new DatabaseHelper(context);
    }

    public QueryFactory Open() throws SQLException
    {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void Close()
    {
        dbHelper.close();
    }

    private int GetMaxRoutine(int programId)
    {
        Cursor c = db.rawQuery(String.format("SELECT COALESCE((MAX(ROUTINE_POS) +1), 1) FROM ROUTINE WHERE PROGRAM_ID = %d", programId), null);
        if(c.getCount() < 1)
            return -1;

        c.moveToFirst();
        return c.getInt(0);
    }

    private int GetMaxExercise(int routineId)
    {
        Cursor c = db.rawQuery(String.format("SELECT COALESCE((MAX(EXERCISE_POS) +1), 1) FROM ROUTINE_EXERCISE WHERE ROUTINE_ID = %d", routineId), null);
        if(c.getCount() < 1)
            return -1;

        c.moveToFirst();
        return c.getInt(0);
    }


    public ArrayList<ProgramDto> SelectPrograms()
    {
        ArrayList<ProgramDto> programDto = new ArrayList<>();

        String query = ProgramQueries.SelectPrograms();
        final Cursor c = db.rawQuery(query, null);

        if (c.getCount() != 0)
        {
            while (c.moveToNext())
            {
                ProgramDto dto = new ProgramDto();
                dto.Id = c.getInt(0);
                dto.Name = c.getString(1);
                dto.RoutineCount = c.getInt(2);

                programDto.add(dto);
            }
            c.close();
        }

        return programDto;
    }

    public ArrayList<RoutineDto> SelectRoutines(int programId)
    {
        ArrayList<RoutineDto> routineDto = new ArrayList<>();

        String query = RoutineQueries.SelectRoutines(programId);
        final Cursor c = db.rawQuery(query, null);

        if (c.getCount() != 0)
        {
            while (c.moveToNext())
            {
                RoutineDto dto = new RoutineDto();
                dto.Id = c.getInt(0);
                dto.Name = c.getString(1);
                dto.Position = c.getInt(2);
                dto.ProgramId = c.getInt(3);
                dto.ExerciseCount = c.getInt(4);

                routineDto.add(dto);
            }
            c.close();
        }
        return routineDto;
    }

    public ArrayList<ExerciseDto> SelectExercises(int routineID)
    {
        ArrayList<ExerciseDto> exerciseDto = new ArrayList<>();

        String query = ExerciseQueries.SelectExercises(routineID);
        final Cursor c = db.rawQuery(query, null);
        if (c.getCount() != 0)
        {
            while(c.moveToNext())
            {
                ExerciseDto dto = new ExerciseDto();
                dto.Id = c.getInt(0);
                dto.Name = c.getString(1);
                dto.Position = c.getInt(2);
                dto.Type = nlubej.gains.Enums.ExerciseType.FromInteger(c.getInt(3));

                exerciseDto.add(dto);
            }
        }
        return exerciseDto;
    }

    public ArrayList<ExerciseDto> SelectAllExercises(int routineId)
    {
        ArrayList<ExerciseDto> exerciseDto = new ArrayList<>();

        String query = ExerciseQueries.SelectAllExercises(routineId);
        final Cursor c = db.rawQuery(query, null);
        if (c.getCount() != 0)
        {
            while(c.moveToNext())
            {
                ExerciseDto dto = new ExerciseDto();
                dto.Id = c.getInt(0);
                dto.Name = c.getString(1);
                dto.Type = nlubej.gains.Enums.ExerciseType.FromInteger(c.getInt(2));

                exerciseDto.add(dto);
            }
        }
        return exerciseDto;
    }

    public ArrayList<ExerciseType> SelecExerciseType()
    {
        String query = ExerciseQueries.SelectExerciseTypes();
        final Cursor c = db.rawQuery(query, null);

        ArrayList<ExerciseType> types = new ArrayList<>();
        if (c.getCount() != 0)
        {
            int i= 0;
            while(c.moveToNext())
            {
                types.add(new ExerciseType(c.getInt(0), c.getString(1)));
            }
        }
        return types;
    }

    public void UpdateRoutineOrder(int[] newIds, List<RoutineDto> dto)
    {
        ContentValues initialValues = new ContentValues();
        for (int i = 0; i < dto.size(); i++)
        {
            initialValues.put("ROUTINE_POS", i + 1);
            db.update("ROUTINE", initialValues, String.format("ROUTINE_ID = %d", newIds[i]), null);
            initialValues.clear();
        }
    }

    public void UpdateExerciseOrder(int[] newIds, ArrayList<ExerciseDto> dto, int routineId)
    {
        ContentValues initialValues = new ContentValues();
        for (int i = 0; i < dto.size(); i++)
        {
            initialValues.put("EXERCISE_POS", i + 1);
            db.update("ROUTINE_EXERCISE", initialValues, String.format("EXERCISE_ID = %d and ROUTINE_ID =%d", newIds[i], routineId), null);
            initialValues.clear();
        }
    }

    public int InsertProgram(String programName)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put("PROGRAM_NAME", programName);

        return (int) db.insert("PROGRAM", null, initialValues);
    }

    public int InsertRoutine(String routineName, int programId)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put("ROUTINE_NAME", routineName);
        initialValues.put("ROUTINE_POS", GetMaxRoutine(programId));
        initialValues.put("PROGRAM_ID", programId);

        return (int) db.insert("ROUTINE", null, initialValues);
    }

    public int InsertExercise(String exerciseName, int exerciseType, int routineId)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put("EXERCISE_NAME", exerciseName);
        initialValues.put("EXERCISE_TYPE", exerciseType);

        return (int) db.insert("EXERCISE", null, initialValues);
    }

    public int InsertRoutineExerciseConnection(int routineId, int exerciseId)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put("ROUTINE_ID", routineId);
        initialValues.put("EXERCISE_ID", exerciseId);
        initialValues.put("EXERCISE_POS", GetMaxExercise(routineId));

        return (int) db.insert("ROUTINE_EXERCISE", null, initialValues);
    }

    public void DeleteProgram(int programId)
    {
        //delete everything from routines
        String deleteRoutinesQuery = ProgramQueries.SelectRoutineIdsForDeletion(programId);
        Cursor cursor = db.rawQuery(deleteRoutinesQuery, null);

        if (cursor.getCount() != 0)
        {
            while(cursor.moveToNext())
            {
                int routineId = cursor.getInt(0);
                DeleteRoutine(routineId);
            }
            cursor.close();
        }

        //delete program
        db.delete("PROGRAM", "PROGRAM_ID = ?", new String[]{String.valueOf(programId)});
    }

    public void DeleteRoutine(int routineId)
    {
        //delete everything from exercises
        String deleteExercisesQuery = RoutineQueries.SelectExerciseIdsForDeletion(routineId);
        Cursor cursor = db.rawQuery(deleteExercisesQuery, null);

        if (cursor.getCount() != 0)
        {
            cursor.moveToNext();
            String exerciseIds = cursor.getString(0);
            cursor.close();

            DeleteExercise(exerciseIds, String.valueOf(routineId));
        }

        //delete routine
        db.delete("ROUTINE", "ROUTINE_ID = ?", new String[]{String.valueOf(routineId)});
    }

    public void DeleteExercise(String exerciseIds, String routineId)
    {
        //delete workout logs
        db.delete("LOGGED_WORKOUT", "EXERCISE_ID IN (?)", new String[]{ exerciseIds});
        //delete notes
        db.delete("WORKOUT_NOTE", "EXERCISE_ID IN (?)", new String[]{ exerciseIds});
        //delete routine exercise connection
        db.delete("ROUTINE_EXERCISE", "EXERCISE_ID IN (?) and ROUTINE_ID =?", new String[]{ exerciseIds, routineId});

        String query = ExerciseQueries.SelectRoutineExerciseConnections(exerciseIds);
        final Cursor c = db.rawQuery(query, null);
        if (c.getCount() == 0)
        {
            //delete exercise
            db.delete("EXERCISE", "EXERCISE_ID IN (?)", new String[]{ exerciseIds});
        }
    }

    public boolean UpdateRoutine(String routineName, int routineId)
    {
        ContentValues args = new ContentValues();
        args.put("ROUTINE_NAME", routineName);
        return db.update("ROUTINE", args, "ROUTINE_ID =?", new String[]{String.valueOf(routineId)}) > 0;
    }

    public boolean UpdateProgram(String programName, int programId)
    {
        ContentValues args = new ContentValues();
        args.put("PROGRAM_NAME", programName);
        return db.update("PROGRAM", args, "PROGRAM_ID =?", new String[]{String.valueOf(programId)}) > 0;
    }

    public boolean UpdateExercise(String exerciseName, int exerciseType, int exerciseId)
    {
        ContentValues args = new ContentValues();
        args.put("EXERCISE_NAME", exerciseName);
        args.put("EXERCISE_TYPE", exerciseType);
        return db.update("EXERCISE", args, "EXERCISE_ID =?", new String[]{String.valueOf(exerciseId) }) > 0;
    }
}
/*



    public long insertLog(int set, double weight, int rep, String date, int workoutNum, String type, long routineId, long exerciseId)
    {
        workoutNum = doubleCheckWorkoutNum(exerciseId,workoutNum);

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_SET, set);
        initialValues.put(KEY_WEIGHT, weight);
        initialValues.put(KEY_REP, rep);
        initialValues.put(KEY_TYPE, type);
        initialValues.put(KEY_DATE, date);
        initialValues.put(KEY_WORKOUT_NUM, workoutNum);
        initialValues.put(KEY_ROUTINEID, routineId);
        initialValues.put(KEY_EXERCISEID, exerciseId);

        return db.insert(DATABASE_TABLE_LOG, null, initialValues);
    }

    public int doubleCheckWorkoutNum(long exerciseId, int num)
    {
        if(num == -1)
        {
            Cursor c = db.rawQuery("SELECT MAX(workoutNum) FROM log WHERE exerciseID = " + exerciseId, null);
            c.moveToFirst();
            num = c.getInt(0);
        }

        return num;
    }

    public long insertLog(int set, String duration, double distance, String date, int workoutNum, String type, long routineId, long exerciseId)
    {
        workoutNum = doubleCheckWorkoutNum(exerciseId,workoutNum);

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_SET, set);
        initialValues.put(KEY_DURATION, duration);
        initialValues.put(KEY_DISTANCE, distance);
        initialValues.put(KEY_TYPE, type);
        initialValues.put(KEY_DATE, date);
        initialValues.put(KEY_WORKOUT_NUM, workoutNum);
        initialValues.put(KEY_ROUTINEID, routineId);
        initialValues.put(KEY_EXERCISEID, exerciseId);

        return db.insert(DATABASE_TABLE_LOG, null, initialValues);
    }
    public long insertLog(int set, double weight, int rep, String duration, double distance, String date, int workoutNum, String type, long routineId, long exerciseId)
    {
        workoutNum = doubleCheckWorkoutNum(exerciseId,workoutNum);

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_SET, set);
        initialValues.put(KEY_WEIGHT, weight);
        initialValues.put(KEY_REP, rep);
        initialValues.put(KEY_DURATION, duration);
        initialValues.put(KEY_DISTANCE, distance);
        initialValues.put(KEY_DATE, date);
        initialValues.put(KEY_WORKOUT_NUM, workoutNum);
        initialValues.put(KEY_TYPE, type);
        initialValues.put(KEY_ROUTINEID, routineId);
        initialValues.put(KEY_EXERCISEID, exerciseId);

        return db.insert(DATABASE_TABLE_LOG, null, initialValues);
    }

    public long insertLog(String weight) {

        Cursor c = db.rawQuery("SELECT MAX(workoutNum) FROM log WHERE type LIKE 'weight'", null);
        c.moveToFirst();
        long num = c.getLong(0);

        Log.i("nlubej","last workout num" + num);
        num = num+1;
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        String date = df.format(cal.getTime());

        Log.i("nlubej", "date: " + date);
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_WEIGHT, Double.parseDouble(weight));
        initialValues.put(KEY_SET, 0);
        initialValues.put(KEY_DATE, date);
        initialValues.put(KEY_WORKOUT_NUM, num);
        initialValues.put(KEY_TYPE, "weight");
        initialValues.put(KEY_ROUTINEID, 0);
        initialValues.put(KEY_EXERCISEID, 0);

        return db.insert(DATABASE_TABLE_LOG, null, initialValues);

    }


    public Cursor getLog(long exerciseId) {

        String sql = "exerciseID = " + exerciseId;
        Cursor cursor =  db.query(DATABASE_TABLE_LOG, new String[] {KEY_TYPE, KEY_SET, KEY_WEIGHT,KEY_REP, KEY_DURATION,KEY_DISTANCE, KEY_WORKOUT_NUM, KEY_DATE}, sql, null, null, null, null);
        return cursor;
    }

    public Cursor getWeightLog() {

        String sql = "exerciseID = 0";
        Cursor cursor =  db.query(DATABASE_TABLE_LOG, new String[] {KEY_WEIGHT, KEY_DATE}, sql, null, null, null, null);
        return cursor;
    }

    public Cursor getPrevLog(long exerciseId, int lastWorkoutNum) {
        String sql = "exerciseID = " + exerciseId + " AND workoutNum =" + lastWorkoutNum;
        Cursor cursor =  db.query(DATABASE_TABLE_LOG, new String[] {KEY_TYPE, KEY_SET, KEY_WEIGHT,KEY_REP, KEY_DURATION,KEY_DISTANCE, KEY_WORKOUT_NUM, KEY_DATE}, sql, null, null, null, null);
        return cursor;
    }

    public Cursor getProgram(long programId) throws SQLException
    {
        Cursor mCursor = db.query(true, DATABASE_TABLE_PROGRAM, new String[] { KEY_ID, KEY_NAME}, KEY_ID + "=" + programId, null,null,null,null,null);
        if(mCursor != null)
            mCursor.moveToFirst();

        return mCursor;
    }


    public int getLastWorkoutNum(long exerciseId){

        String sql = "SELECT workoutNum FROM log WHERE exerciseID = "+ exerciseId + " ORDER BY id DESC LIMIT 1;";

        Cursor c = db.rawQuery(sql, null);

        if(!c.moveToFirst())
            return 0;
        else
            return c.getInt(0);
    }

    public Cursor getPrograms()
    {

        Cursor cursor =  db.query(DATABASE_TABLE_PROGRAM, new String[] { KEY_ID, KEY_NAME},
                null, null, null, null, null);
        return cursor;
    }

    public Cursor getRoutines(long programId)
    {
        String stavek = KEY_PROGRAMID + "=" + programId;
        Cursor cursor =  db.query(DATABASE_TABLE_ROUTINE, new String[] { KEY_ID, KEY_NAME}, stavek, null, null, null, "position");
        return cursor;
    }
    public Cursor getRoutiness(long programId)
    {

        Cursor cursor =  db.query(DATABASE_TABLE_ROUTINE, new String[] { KEY_ID, "position", KEY_NAME}, KEY_PROGRAMID + "=" + programId, null, null, null, "position");
        return cursor;
    }


    public Cursor getRoutine(long routineId, long programId) throws SQLException
    {
        Cursor mCursor = db.query(true, DATABASE_TABLE_ROUTINE, new String[] { KEY_ID, KEY_NAME}, KEY_ID + "=" + routineId + " AND " + KEY_PROGRAMID + "=" + programId, null,null,null,null,null);
        if(mCursor != null)
            mCursor.moveToFirst();

        return mCursor;
    }


    public Cursor getAchievements() {
        return db.query(true, DATABASE_TABLE_ACHIEVEMENT_LOG, new String[] {"name","value"}, null, null,null,null,null,null);

    }


    public int numExer(long routineID)
    {
        Cursor cursor =  db.query(DATABASE_TABLE_EXERCISE, new String[] { KEY_ID}, KEY_ROUTINEID + "=" + routineID, null, null, null, null);
        return cursor.getCount();

    }


    public Cursor getRoutineIds(long programId){

        return db.query(DATABASE_TABLE_ROUTINE, new String[] { KEY_ID, }, KEY_PROGRAMID + "=" + programId, null, null, null, "position");

    }

    public Cursor getExerciseFromIds(long[] routines){

        String sql = "SELECT exercise.id," + " exercise.name, routine.name, routine.id FROM exercise, routine WHERE " + KEY_ROUTINEID + " = ";

        for(int i=0; i< routines.length; i++) {
            if(i == routines.length-1){
                sql += routines[i] + " AND " + routines[i] +" = routine.id";
            }
            else {
                sql +=  routines[i] + " AND " + routines[i] + " = routine.id  OR " + KEY_ROUTINEID + " = ";
            }

        }

        sql += " ORDER BY routine.position, exercise.position;";
        //sql += " AND routineId == routine.id";
        Log.d("nlubej","stavek: " + sql);
        return db.rawQuery(sql, null);

        //return db.query(DATABASE_TABLE_EXERCISE, new String[] { KEY_ID, KEY_NAME }, sql, null, null, null, null);


    }


    public String getType(long exerciseId) {
        String type = "";
        Cursor c = db.query(DATABASE_TABLE_EXERCISE, new String[] {KEY_TYPE}, KEY_ID + " = " + exerciseId, null, null, null, null);
        if(c.getCount() != 0) {
            while(c.moveToNext()){
                type =  c.getString(0);
            }
        }
        return type;
    }

    public Cursor getExercises(long routineID)
    {
        return db.query(DATABASE_TABLE_EXERCISE, new String[] { KEY_ID, KEY_NAME, KEY_TYPE}, KEY_ROUTINEID + "=" + routineID, null, null, null, "position");
    }

    public Cursor getExercisess(long routineID)
    {
        return db.query(DATABASE_TABLE_EXERCISE, new String[] { KEY_ID,"position", KEY_NAME, KEY_TYPE}, KEY_ROUTINEID + "=" + routineID, null, null, null, "position");
    }



    public Cursor getExercisesLogData(long exerciseId, String type, double xValue)
    {
        if(type.compareTo("Strength") == 0)
            return db.query(DATABASE_TABLE_LOG, new String[] { KEY_ID, KEY_SET, KEY_WEIGHT,KEY_REP, KEY_DATE}, KEY_EXERCISEID + "=" + exerciseId + " AND " + KEY_WORKOUT_NUM  + " = " + xValue, null, null, null, null);
        else
            return db.query(DATABASE_TABLE_LOG, new String[] { KEY_ID, KEY_SET, KEY_DURATION, KEY_DISTANCE, KEY_DATE}, KEY_EXERCISEID + "=" + exerciseId + " AND " + KEY_WORKOUT_NUM  + " = " + xValue, null, null, null, null);
    }


    //backup
    public Cursor getAllExercisesBackup(long routineId)
    {
        return db.query(DATABASE_TABLE_EXERCISE, new String[] {KEY_ID, KEY_NAME, KEY_TYPE, KEY_ROUTINEID, KEY_PROGRAMID},KEY_ROUTINEID + " = " + routineId , null, null, null, "position");
    }

    public Cursor getRoutinesBackup(long programiD)
    {
        return db.query(DATABASE_TABLE_ROUTINE, new String[] {KEY_ID, KEY_NAME, KEY_PROGRAMID}, KEY_PROGRAMID + " = " + programiD , null, null, null, "position");
    }

    public Cursor getAllProgramsBackup()
    {
        return db.query(DATABASE_TABLE_PROGRAM, new String[] { KEY_ID, KEY_NAME}, null , null, null, null, null);
    }

    public Cursor getAllLogBackup(long routineId, long exerciseId)
    {
        return db.query(DATABASE_TABLE_LOG, new String[] {KEY_SET, KEY_WEIGHT, KEY_REP, KEY_DURATION, KEY_DISTANCE, KEY_DATE, KEY_WORKOUT_NUM, KEY_TYPE}, KEY_ROUTINEID + "=" + routineId + " AND " + KEY_EXERCISEID + "=" + exerciseId , null, null, null, null);
    }
    public Cursor getAllSettingsBackup()
    {
        return db.query(DATABASE_TABLE_USER, new String[] { KEY_ID, KEY_NAME, KEY_TYPE}, null , null, null, null, null);
    }

    public boolean deleteProgram(long programID)
    {
        return db.delete(DATABASE_TABLE_PROGRAM, KEY_ID + "=" + programID, null) > 0;
    }

    public boolean deleteRoutine(long routineID)
    {
        return db.delete(DATABASE_TABLE_ROUTINE, KEY_ID + "=" + routineID, null) > 0;
    }

    public boolean deleteRoutineByProgram(long programId)
    {
        return db.delete(DATABASE_TABLE_ROUTINE, KEY_PROGRAMID + "=" + programId, null) > 0;
    }

    public boolean deleteExercise(long exerciseID)
    {
        return db.delete(DATABASE_TABLE_EXERCISE, KEY_ID + "=" + exerciseID, null) > 0;
    }

    public boolean deleteExercisesByRoutine(long routineID)
    {
        Log.d("nlubej", "deleted " + routineID);
        return db.delete(DATABASE_TABLE_EXERCISE, KEY_ROUTINEID + "=" + routineID, null) > 0;
    }

    public boolean deleteExercisesByProgram(long programId)
    {
        return db.delete(DATABASE_TABLE_EXERCISE, KEY_PROGRAMID + "=" + programId, null) > 0;
    }

    public boolean deleteLogByProgram(long programId) {
        return db.delete(DATABASE_TABLE_LOG, KEY_PROGRAMID + "=" + programId, null) > 0;

    }



    public boolean deleteLogByExercise(long exerciseId) {
        return db.delete(DATABASE_TABLE_LOG, KEY_EXERCISEID + "=" + exerciseId, null) > 0;

    }


    public boolean deleteLog(int numWorkout, int set,  long exerciseId, long routineId)
    {
        return db.delete(DATABASE_TABLE_LOG, KEY_WORKOUT_NUM + " = " + numWorkout + " AND " + KEY_EXERCISEID + " = " + exerciseId + " AND " + KEY_ROUTINEID + " = " + routineId + " AND " + KEY_SET + " = " + set, null) > 0;
    }

    public boolean deleteRoutines(long programID)
    {
        return db.delete(DATABASE_TABLE_EXERCISE, KEY_ID + "=" + programID, null) > 0;
    }





    public boolean updateProgram(long rowId, String name, int routineId, String comment) {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, name);
        args.put(KEY_NOTE, comment);
        args.put(KEY_ROUTINEID, routineId);

        return db.update(DATABASE_TABLE_PROGRAM, args, KEY_ID + "=" + rowId, null) > 0;
    }


    public boolean updateLogAfterDelete(int set, int workoutNum, int PrevSet,long routineId, long exerciseId)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_SET, set );

        return db.update(DATABASE_TABLE_LOG, initialValues, KEY_WORKOUT_NUM + "=" + workoutNum  + " AND " + KEY_ROUTINEID + "=" + routineId + " AND " + KEY_EXERCISEID + " = " + exerciseId + " AND " + KEY_SET  + " = " + PrevSet, null) > 0;
    }


    public void updateRoutineOrder(long[] positions, long[] ids)
    {
        ContentValues initialValues = new ContentValues();
        for(int i=0; i<ids.length; i++) //zamenjamo idje z -(id) zato da se ne zame�ajo
        {
            initialValues.put(KEY_POSITION, i+1);
            Log.i("nlubej","Update id SET position = " + (i+1) + " WHERE ID = " + ids[(int) positions[i]-1]);
            db.update(DATABASE_TABLE_ROUTINE, initialValues,KEY_ID + " = " +ids[(int) positions[i]-1],  null);
            initialValues.clear();
        }

        Cursor c = getRoutiness(1);
        if(c.getCount() != 0)
        {
            while(c.moveToNext())
            {
                Log.i("nlubej","Routine Id: " + c.getLong(0) + " position: " + c.getString(1) + " name: " + c.getString(2));
            }
        }
    	
    	
    	
    /*
    	for(int i=0; i<ids.length; i++) {
    		ContentValues initialValues = new ContentValues();
    	    initialValues.put(KEY_NAME, currentNames[i]);
    	    eror = db.update(DATABASE_TABLE_ROUTINE, initialValues, KEY_ID+ " =  " + ids[i], null) > 0;	
    	    if(eror)
    	    {
    	    	prevNames[i] = currentNames[i];
    	    }
    	}
    	return prevNames;*/

/*
    public void updateExerciseOrder(long[] positions, long[] ids)
    {
        ContentValues initialValues = new ContentValues();
        for(int i=0; i<ids.length; i++) //zamenjamo idje z -(id) zato da se ne zame�ajo
        {
            initialValues.put(KEY_POSITION, i+1);
            Log.i("nlubej","Update id SET position = " + (i+1) + " WHERE ID = " + ids[(int) positions[i]-1]);
            db.update(DATABASE_TABLE_EXERCISE, initialValues,KEY_ID + " = " +ids[(int) positions[i]-1],  null);
            initialValues.clear();
        }


        Cursor c = getExercisess(1);
        if(c.getCount() != 0)
        {
            while(c.moveToNext())
            {
                Log.i("nlubej","Routine Id: " + c.getLong(0) + " position: " + c.getString(1) + " name: " + c.getString(2));
            }
        }
    }



    public boolean updateRoutine(int set, String duration, int workoutNum, long routineId, long exerciseId)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_SET, set);
        initialValues.put(KEY_DURATION, duration);
        initialValues.put(KEY_WORKOUT_NUM, workoutNum);
        initialValues.put(KEY_ROUTINEID, routineId);
        initialValues.put(KEY_EXERCISEID, exerciseId);

        return db.update(DATABASE_TABLE_LOG, initialValues, KEY_WORKOUT_NUM + "=" + workoutNum  + " AND " + KEY_ROUTINEID + "=" + routineId + " AND " + KEY_EXERCISEID + " = " + exerciseId, null) > 0;
    }

    public Cursor getAllExercises(int programID) {
        Cursor cursor =  db.query(DATABASE_TABLE_EXERCISE, new String[] { KEY_ID, KEY_NAME}, KEY_PROGRAMID + "=" + programID, null, null, null, null);
        return cursor;

    }

    public void test(int numworkout, long exerciseid, long routineid) {

        String sql = "SELECT max(log.weight) FROM log  WHERE " + KEY_ROUTINEID + " = " + routineid + " AND " + KEY_EXERCISEID + " = " + exerciseid + " AND (";

        for(int i=1; i< numworkout+1; i++) {
            if(i == numworkout){
                sql += KEY_WORKOUT_NUM +" = " + i + ")";
            }
            else {
                sql +=  KEY_WORKOUT_NUM + " = " + i + " OR ";
            }

        }
        //sql += " AND routineId == routine.id";

        Log.d("nlubej","stavek: " + sql);

        Cursor c = db.rawQuery(sql, null);
        Log.d("nlubej", "c count : " + c.getCount());
        if(c.getCount() != 0) {

            while(c.moveToNext()){

                Log.d("nlubej", "max weight : " + c.getDouble(0));
            }
        }

    }


    public boolean updateExercise(long exerciseId, String type, String name)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TYPE, type);
        initialValues.put(KEY_NAME, name);

        return db.update(DATABASE_TABLE_EXERCISE, initialValues, KEY_ID + "=" + exerciseId , null) > 0;
    }

    public boolean updateRoutine(long routineId, String routineName) {
        Log.d("nlubej","routineID" + routineId);
        Log.d("nlubej","routineName" + routineName);

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, routineName);

        return db.update(DATABASE_TABLE_ROUTINE, initialValues, KEY_ID + "=" + routineId , null) > 0;

    }

    public boolean updateProgram(long programId, String programName) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, programName);

        Log.d("nlubej", "program name v db: " + programName);

        return db.update(DATABASE_TABLE_PROGRAM, initialValues, KEY_ID + "=" + programId , null) > 0;
    }

    public boolean updateNote(long exerciseId, String note, int workoutNum) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NOTE, note);

        return db.update(DATABASE_TABLE_NOTE, initialValues, KEY_EXERCISEID + " = " + exerciseId + " AND " + KEY_WORKOUT_NUM  + "=" + workoutNum , null) > 0;
    }

    public void insertNote(long exerciseId, String note, int workoutNum) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NOTE, note);
        initialValues.put(KEY_EXERCISEID, exerciseId);
        initialValues.put(KEY_WORKOUT_NUM, workoutNum);

        db.insert(DATABASE_TABLE_NOTE, null, initialValues);
    }

    public String getNote(long exerciseId, int workoutNum) {
        String note = "";
        Cursor c = db.query(DATABASE_TABLE_NOTE, new String[] {KEY_NOTE}, KEY_EXERCISEID + " = " + exerciseId + " AND " + KEY_WORKOUT_NUM  + "=" + workoutNum, null, null, null, null);
        if(c.getCount() != 0) {
            while(c.moveToNext()){
                note =  c.getString(0);
            }
            return note;
        }
        else
        {
            return null;
        }
    }


    public void removeAndCreate() {
        dbHelper.Close();

        Log.d("nlubej","db deleted: " + context.getDatabasePath(DATABASE_NAME).delete());
        //dbHelper.createDb();

    }

    public boolean updateLog(int set, int lastWorkoutNum, long exerciseid, double weight, int reps) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_WEIGHT, weight);
        initialValues.put(KEY_REP, reps);

        return db.update(DATABASE_TABLE_LOG, initialValues, KEY_WORKOUT_NUM + "=" + lastWorkoutNum  + " AND " + KEY_EXERCISEID + "=" + exerciseid + " AND " + KEY_SET+ " = " + set, null) > 0;
    }

    public boolean updateLog(int set, int lastWorkoutNum, long exerciseid, String duration, double distance) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_DURATION, duration);
        initialValues.put(KEY_DISTANCE, distance);

        return db.update(DATABASE_TABLE_LOG, initialValues, KEY_WORKOUT_NUM + "=" + lastWorkoutNum  + " AND " + KEY_EXERCISEID + "=" + exerciseid + " AND " + KEY_SET+ " = " + set, null) > 0;
    }

    public Cursor getUserData() {
        return db.query(DATABASE_TABLE_USER, new String[] {"weight", "height", "tutorialStart", "tutorialMain"},null, null, null, null, null);
    }

    public void insertUser() {
        ContentValues initialValues = new ContentValues();
        initialValues.put("weight", 0);
        initialValues.put("height", "0");
        initialValues.put("tutorialStart", 1);
        initialValues.put("tutorialMain", 1);

        db.insert(DATABASE_TABLE_USER, null, initialValues);
    }

    public void insertAchievements()
    {

        ContentValues initialValues = new ContentValues();
        String[] achievements = new String[]{"workouts10", "workouts30", "workouts60", "workouts80",
                "workouts120", "squatWorkout", "weightLoss", "weightGain", "newPr", "run1", "run5",
                "run10", "run20", "makingGains"};
        int[] values = new int[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0};

        for(int i=0; i< achievements.length; i++)
        {
            initialValues.put("name", achievements[i]);
            initialValues.put("value", values[i]);
            db.insert(DATABASE_TABLE_ACHIEVEMENT_LOG, null, initialValues);
            initialValues.clear();
        }
    }


    public boolean getShowTutorial(String string) {
        if(string.compareTo("main") == 0)
        {
            String sql = "SELECT tutorialMain FROM user;";
            Cursor c = db.rawQuery(sql, null);
            c.moveToFirst();
            if(c.getInt(0) == 1)
                return true;
            else
                return false;
        }
        else
        {
            String sql = "SELECT tutorialStart FROM user;";
            Cursor c = db.rawQuery(sql, null);
            c.moveToFirst();
            if(c.getInt(0) == 1)
                return true;
            else
                return false;
        }
    }

    public boolean updateUser(String base, String string)
    {
        Log.i("nlubej","se pride");
        ContentValues initialValues = new ContentValues();
        initialValues.put(base, string);
        Log.i("nlubej","base: " +base);
        return db.update("user", initialValues,  null,  null) > 0;

    }

    public boolean updateUser(String a1, String a2, String a3, String a4) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("height", a1);
        initialValues.put("weight", a2);
        initialValues.put("tutorialStart", a3);
        initialValues.put("tutorialMain", a4);

        return db.update("user", initialValues,  null,  null) > 0;

    }

    public void insertPrograms() {
        initProgram1();
        initProgram2();

    }

    private void initProgram2() {
        //PROGRAM
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, "Developers Program");
        db.insert(DATABASE_TABLE_PROGRAM, null, initialValues);

        //ROUTINES
        initialValues.clear();
        initialValues.put(KEY_POSITION, 1);
        initialValues.put(KEY_NAME, "Sunday | Legs (Low Reps)");
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_ROUTINE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 2);
        initialValues.put(KEY_NAME, "Monday | Chest/Arms/Abs)");
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_ROUTINE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 3);
        initialValues.put(KEY_NAME, "Wednesday | Back/Shoulders");
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_ROUTINE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 4);
        initialValues.put(KEY_NAME, "Thursday | Legs/Chest (High Reps)");
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_ROUTINE, null, initialValues);

        //EXERCISES
        initialValues.clear();
        initialValues.put(KEY_POSITION, 1);
        initialValues.put(KEY_NAME, "Leg Extensions (Warm-Up)");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 4);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 2);
        initialValues.put(KEY_NAME, "Squats");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 4);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 3);
        initialValues.put(KEY_NAME, "Leg Press");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 4);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 4);
        initialValues.put(KEY_NAME, "Seated Leg Curl");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 4);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 5);
        initialValues.put(KEY_NAME, "Stiff Leg Deadlifts");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 4);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 6);
        initialValues.put(KEY_NAME, "Calf Raises");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 4);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 7);
        initialValues.put(KEY_NAME, "Press Calf Raises");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 4);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        //DIFFERENT ROUTINE

        initialValues.clear();
        initialValues.put(KEY_POSITION, 1);
        initialValues.put(KEY_NAME, "Flyes (Warm-up)");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 5);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 2);
        initialValues.put(KEY_NAME, "Bench Press");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 5);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 3);
        initialValues.put(KEY_NAME, "Incline Dumbbell Press");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 5);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 4);
        initialValues.put(KEY_NAME, "Cable Crossover");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 5);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 5);
        initialValues.put(KEY_NAME, "Hammer Curls");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 5);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 6);
        initialValues.put(KEY_NAME, "Triceps Extensions");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 5);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 7);
        initialValues.put(KEY_NAME, "Bicep Curls");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 5);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 8);
        initialValues.put(KEY_NAME, "Close Grip Bench Press");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 5);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 9);
        initialValues.put(KEY_NAME, "Dips (To Failure)");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 5);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 10);
        initialValues.put(KEY_NAME, "Abs");
        initialValues.put(KEY_TYPE, "Cardio");
        initialValues.put(KEY_ROUTINEID, 5);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);




        //DIFFERENT ROUTINE

        initialValues.clear();
        initialValues.put(KEY_POSITION, 1);
        initialValues.put(KEY_NAME, "Pullups");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 6);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 2);
        initialValues.put(KEY_NAME, "Barbell Rows");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 6);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 3);
        initialValues.put(KEY_NAME, "T-Bar Rows");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 6);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 4);
        initialValues.put(KEY_NAME, "Close Grip Pulldowns");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 6);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 5);
        initialValues.put(KEY_NAME, "Cable Rows");
        initialValues.put(KEY_TYPE, "Cardio");
        initialValues.put(KEY_ROUTINEID, 6);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 6);
        initialValues.put(KEY_NAME, "Deadlifts");
        initialValues.put(KEY_TYPE, "Cardio");
        initialValues.put(KEY_ROUTINEID, 6);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 7);
        initialValues.put(KEY_NAME, "Shoulder Press");
        initialValues.put(KEY_TYPE, "Cardio");
        initialValues.put(KEY_ROUTINEID, 6);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 8);
        initialValues.put(KEY_NAME, "Side Lateral Raise");
        initialValues.put(KEY_TYPE, "Cardio");
        initialValues.put(KEY_ROUTINEID, 6);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 9);
        initialValues.put(KEY_NAME, "Front Lateral Raise");
        initialValues.put(KEY_TYPE, "Cardio");
        initialValues.put(KEY_ROUTINEID, 6);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 10);
        initialValues.put(KEY_NAME, "Machine Shoulder Press");
        initialValues.put(KEY_TYPE, "Cardio");
        initialValues.put(KEY_ROUTINEID, 6);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 11);
        initialValues.put(KEY_NAME, "Shrugs");
        initialValues.put(KEY_TYPE, "Cardio");
        initialValues.put(KEY_ROUTINEID, 6);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);


        //DIFFERENT ROUTINE

        initialValues.clear();
        initialValues.put(KEY_POSITION, 1);
        initialValues.put(KEY_NAME, "Leg Extensions (Warm-up)");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 7);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 2);
        initialValues.put(KEY_NAME, "Squats");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 7);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 3);
        initialValues.put(KEY_NAME, "Leg Press");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 7);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 4);
        initialValues.put(KEY_NAME, "Stiff Leg Deadlift");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 7);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 5);
        initialValues.put(KEY_NAME, "Calf Raises");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 7);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 6);
        initialValues.put(KEY_NAME, "Press Calf Raises");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 7);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 7);
        initialValues.put(KEY_NAME, "Bench Press");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 7);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 8);
        initialValues.put(KEY_NAME, "Incline Dumbbell Press");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 7);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 9);
        initialValues.put(KEY_NAME, "Cable Crossovers");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 7);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 10);
        initialValues.put(KEY_NAME, "Pushups");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 7);
        initialValues.put(KEY_PROGRAMID, 2);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

    }

    private void initProgram1() {
        //PROGRAM
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, "Beginner Workout");
        db.insert(DATABASE_TABLE_PROGRAM, null, initialValues);

        //ROUTINES
        initialValues.clear();
        initialValues.put(KEY_POSITION, 1);
        initialValues.put(KEY_NAME, "Chest/Arms");
        initialValues.put(KEY_PROGRAMID, 1);
        db.insert(DATABASE_TABLE_ROUTINE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 2);
        initialValues.put(KEY_NAME, "Legs");
        initialValues.put(KEY_PROGRAMID, 1);
        db.insert(DATABASE_TABLE_ROUTINE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 3);
        initialValues.put(KEY_NAME, "Back/Shoulders");
        initialValues.put(KEY_PROGRAMID, 1);
        db.insert(DATABASE_TABLE_ROUTINE, null, initialValues);


        //EXERCISES
        initialValues.clear();
        initialValues.put(KEY_POSITION, 1);
        initialValues.put(KEY_NAME, "Bench Press");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 1);
        initialValues.put(KEY_PROGRAMID, 1);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 2);
        initialValues.put(KEY_NAME, "Incline Dumbbell Press");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 1);
        initialValues.put(KEY_PROGRAMID, 1);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 3);
        initialValues.put(KEY_NAME, "Chest Flyes");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 1);
        initialValues.put(KEY_PROGRAMID, 1);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 4);
        initialValues.put(KEY_NAME, "Bicep Curls");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 1);
        initialValues.put(KEY_PROGRAMID, 1);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 5);
        initialValues.put(KEY_NAME, "Triceps Extentions");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 1);
        initialValues.put(KEY_PROGRAMID, 1);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 6);
        initialValues.put(KEY_NAME, "Dips (To Failure)");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 1);
        initialValues.put(KEY_PROGRAMID, 1);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        //DIFFERENT ROUTINE

        initialValues.clear();
        initialValues.put(KEY_POSITION, 1);
        initialValues.put(KEY_NAME, "Leg Extensions (Warm-up)");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 2);
        initialValues.put(KEY_PROGRAMID, 1);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 2);
        initialValues.put(KEY_NAME, "Back Squats");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 2);
        initialValues.put(KEY_PROGRAMID, 1);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 3);
        initialValues.put(KEY_NAME, "Lunges");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 2);
        initialValues.put(KEY_PROGRAMID, 1);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 4);
        initialValues.put(KEY_NAME, "Hamstring Curls");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 2);
        initialValues.put(KEY_PROGRAMID, 1);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 5);
        initialValues.put(KEY_NAME, "Stiff-Leg Deadlift");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 2);
        initialValues.put(KEY_PROGRAMID, 1);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 6);
        initialValues.put(KEY_NAME, "Calf Raises (High reps)");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 2);
        initialValues.put(KEY_PROGRAMID, 1);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        //DIFFERENT ROUTINE


        initialValues.clear();
        initialValues.put(KEY_POSITION, 1);
        initialValues.put(KEY_NAME, "Wide Grip Pull Ups");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 3);
        initialValues.put(KEY_PROGRAMID, 1);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 2);
        initialValues.put(KEY_NAME, "Barbell Bent-Over Row");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 3);
        initialValues.put(KEY_PROGRAMID, 1);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 3);
        initialValues.put(KEY_NAME, "Cable Rows");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 3);
        initialValues.put(KEY_PROGRAMID, 1);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 4);
        initialValues.put(KEY_NAME, "Barbell Shoulder Press");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 3);
        initialValues.put(KEY_PROGRAMID, 1);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 5);
        initialValues.put(KEY_NAME, "Side Lateral Raises");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 3);
        initialValues.put(KEY_PROGRAMID, 1);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);

        initialValues.clear();
        initialValues.put(KEY_POSITION, 6);
        initialValues.put(KEY_NAME, "Front Lateral Raises");
        initialValues.put(KEY_TYPE, "Strength");
        initialValues.put(KEY_ROUTINEID, 3);
        initialValues.put(KEY_PROGRAMID, 1);
        db.insert(DATABASE_TABLE_EXERCISE, null, initialValues);


    }

    public void updateLogNumbers(long exerciseId, int workoutNum) {

        db.delete(DATABASE_TABLE_LOG, KEY_EXERCISEID + "=" + exerciseId + " AND " + KEY_WORKOUT_NUM + " = " + workoutNum, null);
        db.delete(DATABASE_TABLE_NOTE, KEY_EXERCISEID + "=" + exerciseId + " AND " + KEY_WORKOUT_NUM + " = " + workoutNum, null);

        String sql1 = "UPDATE log set workoutNum = workoutNum-1 WHERE + " +  KEY_EXERCISEID + " = " + exerciseId + " AND " + KEY_WORKOUT_NUM + " > " + workoutNum+ ";";
        String sql2 = "UPDATE note set workoutNum = workoutNum-1 WHERE + " +  KEY_EXERCISEID + " = " + exerciseId + " AND " + KEY_WORKOUT_NUM + " > " + workoutNum+ ";";
        db.execSQL(sql1);
        db.execSQL(sql2);
    }
    public boolean updateLog(int set, int lastWorkoutNum, long exerciseId,
                             String duration, double distance, int i) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_DURATION, duration);
        initialValues.put(KEY_DISTANCE, distance);

        return db.update(DATABASE_TABLE_LOG, initialValues, KEY_WORKOUT_NUM + "=" + lastWorkoutNum  + " AND " + KEY_EXERCISEID + "=" + exerciseId + " AND " + KEY_SET+ " = " + set, null) > 0;


    }

    public Cursor getComments() {
        // TODO Auto-generated method stub
        return db.query(DATABASE_TABLE_NOTE, new String[] {"id, note, workoutNum, exerciseID"}, null, null, null, null, null);
    }*/
//}


