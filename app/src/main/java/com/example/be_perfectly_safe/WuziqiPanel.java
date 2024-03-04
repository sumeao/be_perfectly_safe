package com.example.be_perfectly_safe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;


public class WuziqiPanel extends View{
    //棋盘宽度
    private int mPanelWidth;
    //棋盘格子的行高(声明为int会造成由于不能整除而造成的误差较大)
    private float mLineHeight;
    //棋盘最大行数
    private int MAX_LINE_NUM = 10;

    //定义画笔绘制棋盘格子
    private Paint mPaint = new Paint();
    //定义黑白棋子Bitmap
    private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;

    //棋子的缩放比例(行高的3/4)
    private float pieceScaleRatio = 3 * 1.0f / 4;

    //存储黑白棋子的坐标
    private ArrayList<Point> mWhiteArray = new ArrayList<>();
    private ArrayList<Point> mBlackArray = new ArrayList<>();
    //哪方先下子
    private boolean isWhiteFirst = true;

    //游戏是否结束
    private boolean isGameOver;
    //确定赢家
    private final int INTT_WIN = -1;
    public static final int WHITE_WIN = 0;
    public static final int BLACK_WIN = 1;
    public static final int NO_WIN = 2;

    private int mGameWinResutt = INTT_WIN;
    //游戏结束监听
    private OnGameStatusChangeListener listener;

    public void setOnGameStatusChangeListener(OnGameStatusChangeListener listener) {
        this.listener = listener;
    }

    public WuziqiPanel(Context context) {
        this(context, null);
    }

    public WuziqiPanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WuziqiPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化方法
     */
    private void init() {
        //初始化画笔
        mPaint.setColor(0x88000000);
        //设置抗锯齿
        mPaint.setAntiAlias(true);
        //设置防抖动
        mPaint.setDither(true);
        //设置为空心(画线)
        mPaint.setStyle(Paint.Style.STROKE);

        //初始化棋子
        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.icon_white_piece);
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.icon_black_piece);
    }

    /**
     * 测量
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize, heightSize);

        //此处的逻辑判断是处理当我们自定义的View被嵌套在ScrollView中时,获得的测量模式
        // 会是UNSPECIFIED
        // 使得到的widthSize或者heightSize为0
        if (widthMode == MeasureSpec.UNSPECIFIED){
            width = heightSize;
        }else if (heightMode == MeasureSpec.UNSPECIFIED){
            width = widthSize;
        }
        Log.d("pyh", "onMeasure: width" + width + "height" + heightSize);
        //调用此方法使我们的测量结果生效
        setMeasuredDimension(width, width);
    }

    /**
     * 当宽高发生变化时回调此方法
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("pyh", "onSizeChanged: w" + w + "h" + h);

        //此处的参数w就是在onMeasure()方法中设置的自定义View的大小
        //计算出棋盘宽度和行高
        mPanelWidth = w;
        mLineHeight = mPanelWidth * 1.0f / MAX_LINE_NUM;

        //将棋子根据行高变化
        int pieceWidth = (int) (pieceScaleRatio * mLineHeight);
        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceWidth, pieceWidth, false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceWidth, pieceWidth, false);
    }

    /**
     * 进行绘制工作
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBoard(canvas);
        drawPieces(canvas);
        checkGameOver();
    }

    /**
     * 绘制棋盘
     * @param canvas
     */
    private void drawBoard(Canvas canvas) {
        int w = mPanelWidth;
        float lineHeight = mLineHeight;

        for (int i = 0; i < MAX_LINE_NUM; i++) {
            int startX = (int) (lineHeight / 2);
            int endX = (int) (w - lineHeight / 2);

            int y = (int) ((0.5 + i) * lineHeight);
            //画横线
            canvas.drawLine(startX, y, endX, y, mPaint);
            //画竖线
            canvas.drawLine(y, startX, y, endX, mPaint);
        }
    }

    /**
     * 绘制棋子
     * @param canvas
     */
    private void drawPieces(Canvas canvas) {
        //绘制白棋子
        for (int i = 0, n = mWhiteArray.size(); i < n; i++) {
            Point whitePoint = mWhiteArray.get(i);
            //棋子之间的间隔为1/4行高
            canvas.drawBitmap(mWhitePiece,
                    (whitePoint.x + (1 - pieceScaleRatio) / 2) * mLineHeight,
                    (whitePoint.y + (1 - pieceScaleRatio) / 2) * mLineHeight, null);
        }
        //绘制黑棋子
        for (int i = 0, n = mBlackArray.size(); i < n; i++) {
            Point blackPoint = mBlackArray.get(i);
            //棋子之间的间隔为1/4行高
            canvas.drawBitmap(mBlackPiece,
                    (blackPoint.x + (1 - pieceScaleRatio) / 2) * mLineHeight,
                    (blackPoint.y + (1 - pieceScaleRatio) / 2) * mLineHeight, null);
        }
    }

    private boolean checkNoWin(boolean whiteWin, boolean blackWin){
        if (whiteWin || blackWin){
            return false;
        }
        int maxPieces = MAX_LINE_NUM * MAX_LINE_NUM;
        if (mWhiteArray.size() + mBlackArray.size() == maxPieces){
            return true;
        }
        return false;
    }



    /**
     * 处理用户手势操作
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isGameOver) return false;

        int action = event.getAction();
        //手指抬起后处理
        if (action == MotionEvent.ACTION_UP){

            //拦截事件自己来处理
            int x = (int) event.getX();
            int y = (int) event.getY();
            Log.d("pyh1", "onTouchEvent: fx" + event.getX() + "fy" + event.getY() + "ix" + x + "iy" + y);
            Point point = getValidPoint(x, y);
            if (mWhiteArray.contains(point) || mBlackArray.contains(point)){
                return false;
            }
            if (isWhiteFirst){
                mWhiteArray.add(point);
            }else{
                mBlackArray.add(point);
            }
            //调用重绘
            invalidate();
            isWhiteFirst = !isWhiteFirst;
        }
        return true;
    }

    /**
     * 将用户点击的位置的Point转换为类似于(0,0)d的坐标
     * @param x
     * @param y
     * @return
     */
    private Point getValidPoint(int x, int y) {
        Log.d("pyh1", "getValidPoint: lx" + (int) (x / mLineHeight) + "ly" + (int) (y / mLineHeight));
        return new Point((int) (x / mLineHeight), (int) (y / mLineHeight));
    }

    /**
     * 检查游戏是否结束
     */
    private void checkGameOver() {
        //检查是否五子连珠
        boolean whiteWin = WuziqiUtil.checkFiveInLine(mWhiteArray);
        boolean blackWin = WuziqiUtil.checkFiveInLine(mBlackArray);
        boolean noWin = checkNoWin(whiteWin, blackWin);
        if (whiteWin){
            mGameWinResutt = WHITE_WIN;
        }else if (blackWin){
            mGameWinResutt = BLACK_WIN;
        }else if (noWin){
            mGameWinResutt = NO_WIN;
        }
        if (whiteWin || blackWin || noWin){
            isGameOver = true;
            if (listener != null){
                listener.onGameOver(mGameWinResutt);
            }
        }

    }
    /**
     * 重新开始,再来一局
     */
    public void restartGame(){
        mBlackArray.clear();
        mWhiteArray.clear();
        isGameOver = false;
        mGameWinResutt = INTT_WIN;
//        重绘
        invalidate();
    }

    /**
     * 防止内存不足活动被回收
     */
    private static final String INSTANCE = "instance";
    private static final String INSTANCE_GAME_OVER = "instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY = "instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY = "instance_black_array";


    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE, super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER, isGameOver);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY, mWhiteArray);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY, mBlackArray);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle){
            Bundle bundle = (Bundle) state;
            isGameOver = bundle.getBoolean(INSTANCE_GAME_OVER);
            mWhiteArray = bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            mBlackArray = bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }



    /**
     * 游戏结束回调监听
     */
    public interface OnGameOverListener{
        void gameOver(boolean isWhiterWinner);
    }

    /**
     * 设置谁先下棋
     */
    public void setFirstPiece(boolean isWhiteFirst){
        this.isWhiteFirst = isWhiteFirst;
    }
}
