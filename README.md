# 룸 예제 프로젝트
### 시작하기전
#### :smirk: Room 이란
안드로이드 아키텍쳐((Android Architecture Components) 중 하나로서 Room은 SQLite의 추상 레이어를 제공하여 SQLite의 객체를 매핑하는 역할을 한다.
쉽게 말하면 SQLite의 기능을 모두 사용할 수 있고, DB로의 접근을 편하게 도와주는 라이브러리 이다.

근데 왜 SQLite를 직접 쓰지 않고 굳이 Room을 쓰는지 궁금할 수 있다. 그 이유는  [Android Developers Page SQLite](https://developer.android.com/training/data-storage/sqlite) 문서에 ‘Caution’ 마크와 함께 다음과 같이 설명이 되어있다.

+ 원본 SQL은 컴파일 시간이 확실하지 않다. SQL 데이터에 변화가 생기면 수동으로 업데이트 해 주어야 한다. 이 과정에서 시간이 많이 소요되며 오류가 생길 수 있다.
+ SQL 쿼리와 데이터 객체를 변환하기 위해서는 많은 상용구 코드(boilerplate code)를 작성해야 한다.

즉 Room을 사용하면 컴파일 시간을 체크할 수 있으며, 무의미한 boilerplate 코드의 반복을 줄여줄 수 있다. 그리고 LiveData를 쓸수있어서인거 같다. (이건 내생각 :blush: )

Room이 Architecture Components에 포함되는 만큼, Architecture Components 다른 구성 요소인 LiveData나 ViewModel 등과 함께 사용하면 아주 간편하게 데이터베이스를 관리하고 UI를 갱신할 수 있다.
하지만 여기서는 기존 형식의 프로젝트에 SQLite 대신 Room으로 데이터를 저장하는 데에 의의를 두어 예제를 만들었다.

#### Room Components 룸 구성 요소
Room에는 3가지 구성 요소가 있다.

1. Entity - Database 안에 있는 테이블을 Java나 Kotlin 클래스로 나타낸 것이다. 데이터 모델 클래스라고 볼 수 있다.

2. DAO - Database Access Object, 데이터베이스에 접근해서 실질적으로 insert, delete 등을 수행하는 메소드를 포함한다.

3. Database - database holder를 포함하며, 앱에 영구 저장되는 데이터와 기본 연결을 위한 주 액세스 지점이다. RoomDatabase를 extend 하는 추상 클래스여야 하며, 테이블과 버전을 정의하는 곳이다.

<img src = "https://user-images.githubusercontent.com/48902047/132025753-ad0ea617-a825-49ff-9566-58cb0a7424ec.png">

***
### :wrench: 기능설명
+ Recycleview 를 이용하여 저장한 리스트를 보여준다.
+ Room을 이용하여 저장한다.
+ 추가적으로 kotlin을 좀더 활용해본다. 

***

### :lollipop: 완성 화면

<img src = "https://user-images.githubusercontent.com/48902047/132026851-677096ee-44f5-4d56-9cdb-230a6f421c08.jpg" width="20%" height="20%"> <img src = "https://user-images.githubusercontent.com/48902047/132026602-ad9bf959-50b7-45e4-8e97-fda0b8193c64.jpg" width="20%" height="20%">

***

### Dependency 추가
```Kotlin
plugins {
    id 'com.android.application'
    id 'kotlin-android'
    id 'kotlin-kapt' //kapt 쓰게 해주는거
    id 'kotlin-android-extensions' // xml 과 자바 연결시 더러운 코드를 안쓰게 해준다.
}

android {
    compileSdkVersion 30
    buildToolsVersion "30.0.2"

    defaultConfig {
        applicationId "com.example.roomexample"
        minSdkVersion 23
        targetSdkVersion 30
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = '1.8'
    }
}

dependencies {

    implementation "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"
    implementation 'androidx.core:core-ktx:1.3.2'
    implementation 'androidx.appcompat:appcompat:1.2.0'
    implementation 'com.google.android.material:material:1.3.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.0.4'
    testImplementation 'junit:junit:4.+'
    androidTestImplementation 'androidx.test.ext:junit:1.1.2'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.3.0'

    def room_version = "2.2.6"

    implementation "androidx.room:room-runtime:$room_version"
    kapt "androidx.room:room-compiler:$room_version"

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation "androidx.room:room-ktx:$room_version"

    // optional - RxJava support for Room
    implementation "androidx.room:room-rxjava2:$room_version"

    // optional - Guava support for Room, including Optional and ListenableFuture
    implementation "androidx.room:room-guava:$room_version"

    // Test helpers
    testImplementation "androidx.room:room-testing:$room_version"
}
```

### Entity
데이터 모델에 무엇이 들어갈것인지 정의

id 같은 경우 고유해야 하므로 PrimaryKey로 지정해준다.

```Kotlin
@Entity(tableName = "cat")
class Cat(@PrimaryKey var id: Long?,
          @ColumnInfo(name = "catname") var catName: String?,
          @ColumnInfo(name = "lifespan") var lifeSpan: Int,
          @ColumnInfo(name = "origin") var origin: String
){
    constructor(): this(null,"", 0,"")
}
```
### DAO
DB 에 접근해 질의를 수행하는 기능을 한다. Query 메소드로 작성
```Kotlin
@Dao
interface CatDao {
    @Query("SELECT * FROM cat")
    fun getAll(): List<Cat>

    /* import android.arch.persistence.room.OnConflictStrategy.REPLACE */
    @Insert(onConflict = REPLACE)
    fun insert(cat: Cat)

    @Query("DELETE from cat")
    fun deleteAll()
}
```
### Database
Entity 모델을 기반으로 하고, DAO의 메소드를 가지고 있는 데이터베이스를 생성

RoomDatabase()를 상속한다.

MainActivity에서 호출하여 database 객체를 반환하거나 삭제할 수 있도록 getInstance()와 destroyInstance() 메소드를 생성해준다.
```Kotlin
@Database(entities = [Cat::class], version = 1)
abstract class CatDB: RoomDatabase() {
    abstract fun catDao(): CatDao

    companion object {
        private var INSTANCE: CatDB? = null

        fun getInstance(context: Context): CatDB? {
            if (INSTANCE == null) {
                synchronized(CatDB::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            CatDB::class.java, "cat.db")
                            .fallbackToDestructiveMigration()
                            .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
```
### Activity에서 Room에 접근
메인 쓰레드에서 Room DB에 접근하려고 하면 에러가 발생한다. 아래의 IllegalStateException 메세지를 볼 수 있다.

+ Cannot access database on the main thread since it may potentially lock the UI for a long period of time.
(메인 UI 화면이 오랫동안 멈춰있을 수도 있기 때문에, 메인 쓰레드에서는 데이터베이스에 접근할 수 없습니다.)

따라서 Room과 관련된 액션은 Thread, AsyncTask 등을 이용해 백그라운드에서 작업해야 한다.

CatDataBase 의 Room.databaseBuilder를 호출해 새로운 db 객체를 만들고, 데이터 읽기/쓰기는 서브 쓰레드에서 작업하게 될 것이다.
```Kotlin
class MainActivity : AppCompatActivity() {
    private var catDb : CatDB? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        catDb = CatDB.getInstance(this)

        val r = Runnable {
            // 데이터에 읽고 쓸때는 쓰레드 사용
        }

        val thread = Thread(r)
        thread.start()
    }
}
```
알아서 RecyclerView는 만들고....(너무 많이 했으니까 ㅎ)

SQLite에서 바로 View로 데이터를 가져올 수 없으므로 Cat 클래스의 빈 리스트 List<Cat> 을 만들었다.
```Kotlin
/* MainActivity.kt */

private var catList = listOf<Cat>()
```
빈 List로 초기화된 catList에 Room db에 저장된 정보를 모두 읽어와서 Cat 의 형태로 저장한다.
우리가 앞서 CatDao에서 만든 getAll() 메소드를 통해서 가져올 수 있다.
단, 서브 쓰레드를 사용하여 메인 쓰레드에 영향을 주지 않도록 해야 한다.
```Kotlin
val r = Runnable {
    catList = catDb?.catDao()?.getAll()!!
}

val thread = Thread(r)
thread.start()
```
addActivity에서는 새로운 cat을 생성하여 id이외의 값을 지정한 후 BD에 저장해준다.
```Kotlin
/*addActivity.java*/
        val addRunnable = Runnable {
            val newCat = Cat()
            newCat.catName = addName.text.toString()
            newCat.lifeSpan = addLifeSpan.text.toString().toInt()
            newCat.origin = addOrigin.text.toString()
            catDb?.catDao()?.insert(newCat)
        }

        addBtn.setOnClickListener {
            val addThread = Thread(addRunnable)
            addThread.start()

            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            finish()
        }
```


최종적인 MainActivity는 
```Kotlin
class MainActivity : AppCompatActivity() {

    private var catDb : CatDB? = null
    private var catList = listOf<Cat>()
    lateinit var mAdapter : CatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        catDb = CatDB.getInstance(this)
        mAdapter = CatAdapter(this, catList)

        val r = Runnable {
            try {
                catList = catDb?.catDao()?.getAll()!!
                mAdapter = CatAdapter(this, catList)
                mAdapter.notifyDataSetChanged()

                mRecyclerView.adapter = mAdapter
                mRecyclerView.layoutManager = LinearLayoutManager(this)
                mRecyclerView.setHasFixedSize(true)
            } catch (e: Exception) {
                Log.d("tag", "Error - $e")
            }
        }

        val thread = Thread(r)
        thread.start()

        mAddBtn.setOnClickListener {
            val i = Intent(this, AddActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    override fun onDestroy() {
        CatDB.destroyInstance()
        catDb = null
        super.onDestroy()
    }
}
```
***

### :paperclip: 소감
공부해본 결과 ViewModel과 LiveData를 이용하게 되면 Data가 변할 시 자동적으로 변하게 된다. 다음 장은 세개를 다 이용해 보겠다.
















