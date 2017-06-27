package com.lvsijian8.flowerpot.global;

/**
 * Created by Administrator on 2017/3/17.
 */
public class Const {

    public static int selector;
    /**
     * //接收网络数据成功
     */
    public static final int RECEIVE_DATA_SUCCESS=0;
    /**
     * 接收网络数据失败
     */
    public static final int RECEIVE_DATA_FAIL=1;
    /**
     * 接收网络数据为空
     */
    public static final int RECEIVE_DATA_NUll=2;
    /**
     * 施肥成功
     */
    public static final int REMOTE_BOTTLE_SUCCESS=0X11;
    /**
     * 施肥失败
     */
    public static final int REMOTE_BOTTLE_FAIL=0X12;
    /**
     * 浇水成功
     */
    public static final int REMOTE_WATER_SUCCESS=0X13;
    /**
     * 浇水失败
     */
    public static final int REMOTE_WATER_FAIL=0X14;
    /**
     * 刷新植物数据
     */
    public static final int RESHRE_FIND_DATA=0X15;
    /**
     * 加载更多植物数据
     */
    public static final int LOADING_FIND_DATA=0X16;
    /**
     * 登录账号不存在
     */
    public static final int LOGIN_STATE_UNEXIST=0X17;
    /**
     * 登录密码错误
     */
    public static final int LOGIN_STATE_PWDFAIL=0X18;
    /**
     * 登录账号不存在
     */
    public static final int LOGIN_STATE_SUCCESS=0X19;
    /**
     * 注册账号已存在
     */
    public static final int SIGNUP_STATE_REPEAT=0X20;
    /**
     * 注册账号成功
     */
    public static final int SIGNUP_STATE_SUCCESS=0X21;
    /**
     * 添加花盆成功
     */
    public static final int APPEND_STATE_SUCCESS=0X22;
    /**
     * 添加花盆失败
     */
    public static final int APPEND_STATE_FAIL=0X23;
    /**
     * 花盆为空
     */
    public static final int APPEND_STATE_NULL=0X24;

    public static final int SERVICE_REMOTER=0X25;

    public static final String USER_ID="user_id";
    public static final String USER_NAME="user_name";
    public static final String USER_PASSWODR="user_pwd";
    public static final String USER_PHONE="user_phone";
    public static final String FLOWER_NAME="flower_name";

    /*-------------------连接后台的地址-----------------------------------------*/
//    public static final String URL_PATH ="http://172.16.60.25:8080/MrGarden/";
    public static final String URL_PATH ="https://lvsijian.cn/MrGarden/";
    public static final String URL_LOGIN= URL_PATH +"loginAndroid";//登录
    public static final String URL_SIGNUP= URL_PATH +"signupAndroid";//注册
    public static final String URL_DATA= URL_PATH+"fdataAndroid";//消息列表
    public static final String URL_FIND= URL_PATH +"plantAndroid";//植物列表
    public static final String URL_DEVICE= URL_PATH +"deviceAndroid";//植物列表
    public static final String URL_DETAIL= URL_PATH +"plantDetailAndroid";//植物详情列表
    public static final String URL_REMOTE=URL_PATH+"deviceDetailAndroid";//远程界面
    public static final String URL_PIC= URL_PATH + "sql_image";//图片前缀
    public static final String URL_BOTTLE= URL_PATH +"";//浇营养液
    public static final String URL_WATER= URL_PATH +"";//浇水
    public static final String URL_APPEND= URL_PATH +"appendAndroid";//添加花盆
    public static final String URL_ADDGROUP= URL_PATH +"addGroupAndroid";//添加花盆
    public static final String URL_FEEDBACK=URL_PATH+"askMeAndroid";//反馈信息
    public static final String URL_UPDATA=URL_PATH+"updata/updataAndroid.json";//更新
    public static final String URL_SEARCH=URL_PATH+"searchAndroid";//搜寻植物
    public static final String URL_DELETE=URL_PATH+"deletepotAndroid";//删除花盆
    public static final String URL_SETWATER=URL_PATH+"setWaterAndroid";//设置浇水数据
    public static final String URL_SETBOTTLE=URL_PATH+"setBottleAndroid";//设置施肥数据
    public static final String URL_ADDWATER=URL_PATH+"wateringAndroid";//浇水
    public static final String URL_ADDBOTTLE=URL_PATH+"fertilizeringAndroid";//施肥
    public static final String URL_HISTORY=URL_PATH+"historyAndroid";//历史记录界面
    public static final String URL_BATCH_POT=URL_PATH+"getManageAllAndroid";//批量操作界面的数据获取
    public static final String URL_BATCH_WATER=URL_PATH+"waterAllAndroid";//批量操作-浇水
    public static final String URL_BATCH_BOTTLE=URL_PATH+"bottleAllAndroid";//批量操作-施肥
    public static final String URL_GETINFO=URL_PATH+"getUserAndroid";//获取要修改资料的用户的信息
    public static final String URL_ALERTINFO=URL_PATH+"changeUserAndroid";//修改资料
    public static final String URL_SCHOOL=URL_PATH+"schoolListAndroid";//学校资料列表
//    public static final String URL_FORGET=URL_PATH+"schoolListAndroid";//找回密码
    public static final String URL_NEW_DATA=URL_PATH+"gdataAndroid";
    public static final String URL_GET_MENU=URL_PATH+"getGroupPotAndroid";
    public static final String URL_NEW_TIME=URL_PATH+"deviceAndroid";
    public static final String URL_NEW_APPEND=URL_PATH+"getGroupAndroid";
    public static final String URL_FORGET="http://172.16.60.25:8080/MrGarden/findPwdAndroid";//找回密码
    /*--------------------------------------------------------------------------*/

    /*-------------------用于添加页面返回和进入下一个界面时的判断---------------------------*/
    public static final int APPEND_REGISTER=0;
    public static final int APPEND_POT=1;
    public static final int APPEND_DATA=2;
    public static final int APPEMD_NULL=3;
    public static int APPEND_INTSTATE=APPEMD_NULL;
    /*-------------------------------------------------------------------------------------*/

    /*-------------------用于远程界面多个联网操作的判断---------------------------*/
    public static final int REMOTE_CONNECTION_SUCCESS=0;
    public static final int REMOTE_CONNECTION_FAIL=1;//进入远程界面时的状态码
    public static final int REMOTE_BOTTLE_ADD_SUCCESS=2;
    public static final int REMOTE_BOTTLE_ADD_FAIL=3;//点击施肥时的状态码
    public static final int REMOTE_WATER_ADD_SUCCESS=4;
    public static final int REMOTE_WATER_ADD_FAIL=5;//点击浇水时的状态码
    public static final int REMOTE_BOTTLE_MANAGER_SUCCESS=6;
    public static final int REMOTE_BOTTLE_MANAGER_FAIL=7;//点击施肥管理时的状态码
    public static final int REMOTE_WATER_MANAGER_SUCCESS=8;
    public static final int REMOTE_WATER_MANAGER_FAIL=9;//点击浇水管理时的状态码
    public static int REMOTE_STATE_SUCCESS=REMOTE_CONNECTION_SUCCESS;//联网成功失败判断的状态码


    public static final int REMOTE_CONNECTION=0;
    public static final int REMOTE_BOTTLE_ADD=1;
    public static final int REMOTE_WATER_ADD=2;
    public static final int REMOTE_BOTTLE_MANAGER=3;
    public static final int REMOTE_WATER_MANAGER=4;
    public static  int REMOTE_STATE=REMOTE_CONNECTION;
    /*---------------------------------------------------------------------------*/

    public static final int MENU_STATE_DATA=0;
    public static final int MENU_STATE_TIME=1;
    public static int MENU_STATE_CURRENT=MENU_STATE_DATA;
    /*---------------------------------------------------------------------------*/
    public static boolean isResume;



}
