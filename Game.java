package version2;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class Game extends JFrame implements MouseListener, ActionListener, WindowListener {
	public Game() {
	}

	/** 窗口宽度 */
	public final static int WIDTH = 920;

	/** 窗口高度 */
	public final static int HEIGHT = 920;

	/** 棋盘距离上下左右的边界 **/
	public final static int MARGIN = 100;

	/** 棋盘线间距 */
	public final static int SPACING = (HEIGHT - 2 * MARGIN) / 18;

	/** 黑子 */
	public final static int BLACK = 1;

	/** 白子 */
	public final static int WHITE = 2;

	/** 人人对弈 */
	public final static int GAME_TYPE_MANMAN = 1;

	/** 人机对弈 */
	public final static int GAME_TYPE_MANMACHINE = 2;
	/** 网络对弈 */
	public final static int GAME_TYPE_NETWORK = 3;
	/** 菜单条 */
	private JMenuBar menuBar;

	/** 游戏菜单 */
	private JMenu gameMenu;

	/** 人人菜单项 */
	private JMenuItem menuItemManMan;

	/** 人机菜单项 */
	private JMenuItem menuItemManMachine;

	/** 对弈菜单项 */
	private JMenuItem menuItemNetwork;

	/** 游戏大厅菜单项 */
	private JMenuItem menuItemGameLoggy;

	/** 悔棋菜单项 */
	private JMenuItem menuItemUndo;

	/** 中局存盘菜单项 */
	private JMenuItem menuItemSaveGame;

	/** 读取存盘菜单项 */
	private JMenuItem menuItemLoadGame;

	/** 历史战绩菜单项 */
	private JMenuItem menuItemHistory;

	/** 退出菜单项 */
	private JMenuItem menuItemExit;

	/** 棋盘上所有位置的落子状态 */
	private int[][] state = new int[19][19];

	/** 当前落子的颜色 */
	public int curPieceColor = BLACK;

	/** 最后落子行 */
	private int lastRow = -1;

	/** 最后落子列 */
	private int lastColumn = -1;

	/** 用于判断五子输赢 */
	private Judge judge = new Judge();

	/** 对弈类型 */
	private int gameType = GAME_TYPE_MANMACHINE;

	/** 玩家1名称 */
	public String player1 = null;

	/** 玩家2名称 */
	public String player2 = null;

	/** 对弈机器人 */
	private RobotWithGreedy robot = new RobotWithGreedy();

	/**
	 * 将棋盘状态恢复到第一次落子的状态
	 */
	public void reset(int gameType, String player1, String player2) {
		for (int i = 0; i < 19; i++) {
			for (int j = 0; j < 19; j++) {
				state[i][j] = 0;
			}
		}
		this.lastRow = this.lastColumn = -1;
		this.curPieceColor = BLACK;
		this.player1 = player1;
		this.player2 = player2;
		this.gameType = GAME_TYPE_NETWORK;
		this.state = new int[19][19];
		// 3.置游戏状态为初始状态

		this.repaint();

		this.repaint();// 状态更新了，界面却不会发生变化，需要这里通知虚拟机去调用paint
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		// 画棋盘
		drawBoard(g);

		// 显示玩家名称
		drawPlayer(g);

	}

	// 第一个参数是字体，第二个是样式，第二个是字号
	private Font playerFont = new Font("楷体", 0, 14);

	private void drawPlayer(Graphics g) {
		// 设置字体
		g.setFont(playerFont);

		if (this.player1 != null) {
			if (this.curPieceColor == BLACK)
				g.setColor(Color.RED);
			else
				g.setColor(Color.BLACK);
			g.drawString(this.player1, 40, 110); // 后两个参数是字符串显示外接矩形左上角坐标
		}
		if (this.player2 != null) {
			if (this.curPieceColor == WHITE)
				g.setColor(Color.RED);
			else
				g.setColor(Color.BLACK);

			g.drawString(this.player2, 850, 110);
		}
	}

	/**
	 * 画一个黑色棋盘
	 * 
	 * @param g 画布对象
	 */
	private void drawBoard(Graphics g) {
		Color c = new Color(200, 200, 200); // 棋盘背景色，三个值分别为颜色RGB三个分量，取值范围0-255
		g.setColor(c);
		// 画一个被颜色填充的矩形，四个坐标分别为矩形左上角X坐标，Y坐标，矩形宽度和矩形高度
		g.fillRect(MARGIN, MARGIN, WIDTH - 2 * MARGIN, HEIGHT - 2 * MARGIN);

		// 循环19次，每次画一个横线和一个竖线,
		for (int i = 0; i < 19; i++) {

			g.setColor(Color.BLACK); // 棋盘线颜色

			g.drawLine(MARGIN, MARGIN + i * SPACING, WIDTH - MARGIN, i * SPACING + MARGIN); // 画一条横线
			g.drawLine(i * SPACING + MARGIN, MARGIN, i * SPACING + MARGIN, HEIGHT - MARGIN);// 画一条竖线

		}

		// 遍历棋盘状态数组，将其中的黑子和白子显示出来
		for (int i = 0; i < 19; i++) {
			for (int j = 0; j < 19; j++) {
				if (state[i][j] == 0)
					continue;
				if (state[i][j] == BLACK)
					g.setColor(Color.BLACK);
				else if (state[i][j] == WHITE)
					g.setColor(Color.WHITE);

				int x1 = j * SPACING + MARGIN;
				int y1 = i * SPACING + MARGIN;

				g.fillOval(x1 - 10, y1 - 10, 20, 20); // 画一个椭圆，参数分别为椭圆外接矩形的左上角坐标，宽度和高度（画椭圆和圆都是这个函数）
			}
		}
	}

	private void initMenus() {
		// 创建菜单条
		menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		// 创建游戏菜单
		gameMenu = new JMenu("游戏");
		menuBar.add(gameMenu);
		// 创建所有菜单项
		menuItemManMan = new JMenuItem("人人对弈");
		menuItemManMan.addActionListener(this);
		gameMenu.add(menuItemManMan);

		menuItemManMachine = new JMenuItem("人机对弈");
		menuItemManMachine.addActionListener(this);
		gameMenu.add(menuItemManMachine);

		menuItemNetwork = new JMenuItem("网络对弈");
		menuItemNetwork.addActionListener(this);
		gameMenu.add(menuItemNetwork);

		menuItemGameLoggy = new JMenuItem("游戏大厅");
		menuItemGameLoggy.addActionListener(this);
		gameMenu.add(menuItemGameLoggy);
		gameMenu.addSeparator();

		menuItemUndo = new JMenuItem("悔棋");
		menuItemUndo.addActionListener(this);
		gameMenu.add(menuItemUndo);

		menuItemSaveGame = new JMenuItem("中局存盘");
		menuItemSaveGame.addActionListener(this);
		gameMenu.add(menuItemSaveGame);

		menuItemLoadGame = new JMenuItem("读取存盘");
		menuItemLoadGame.addActionListener(this);
		gameMenu.add(menuItemLoadGame);
		menuItemHistory = new JMenuItem("历史战局");
		menuItemHistory.addActionListener(this);
		gameMenu.add(menuItemHistory);

		gameMenu.addSeparator();

		menuItemExit = new JMenuItem("退出游戏");
		menuItemExit.addActionListener(this); // 为”退出游戏“菜单项添加监听对象（重写了actionPerformed函数的对象）
		gameMenu.add(menuItemExit);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Game game = new Game();
		Context.game = game;
		game.setTitle("五子棋");
		game.setSize(WIDTH, HEIGHT);

		// 初始化菜单
		game.initMenus();

		// 添加窗口事件监听对象
		game.addWindowListener(game);

		// 将窗口与鼠标监听关联起来，尽管看起来这行语句比较怪，但它是必须的
		// 第一个game是指窗口，第二个game是实现鼠标监听的对象，在本例中两者凑巧是同一个对象
		game.addMouseListener(game);

		game.setLocationRelativeTo(null);
		game.setResizable(false);
		game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.setVisible(true);
	}

	/**
	 * 根据鼠标窗口点击位置计算对应棋盘的第几行第几列
	 * 
	 * @param x 鼠标位置
	 * @param y 鼠标位置
	 * @return 两个元素的鼠标位置，分别表示棋盘的行和列
	 */
	private int[] positionToRowColumn(int x, int y) {

		// 鼠标没有点在棋盘范围内
		if (x < MARGIN || x > MARGIN + SPACING * 18)
			return null;
		if (y < MARGIN || y > MARGIN + SPACING * 18)
			return null;

		// 判断鼠标点在了棋盘的第几列
		int col = (x - MARGIN) / SPACING; // 点在col和col+1之间
		int t = (x - MARGIN) % SPACING; // 判断是距离col和col+1哪个近
		if (t > SPACING / 2)
			col += 1;
		// 判断鼠标点在了棋盘的第几行
		int row = (y - MARGIN) / SPACING;
		t = (y - MARGIN) % SPACING;
		if (t > SPACING / 2)
			row += 1;

		return new int[] { row, col };
	}

	/**
	 * 网络对方落子
	 * 
	 * @param row 落子行
	 * @param col 落子列
	 * @return 成功落子返回 true
	 * @throws Exception 错误信息
	 */
	public boolean putNetworkpiece(int row, int col) throws Exception {
		// 如果不该对方落子，什么都不做
		if (this.curPieceColor == 0)
			return false;
		if (this.curPieceColor == Game.BLACK && this.player1.equals(Context.account))
			return false;
		if (this.curPieceColor == Game.WHITE && this.player2.equals(Context.account))
			return false;
		// 如果落子位置有子
		if (this.state[row][col] != 0)
			return false;
		// 否则，记录棋盘状态变化
		this.state[row][col] = this.curPieceColor;
		if (this.curPieceColor == Game.BLACK)
			this.curPieceColor = Game.WHITE;
		else
			this.curPieceColor = Game.BLACK;
		return true;
	}

	// 当鼠标按钮按下并抬起后该函数被虚拟机调用
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if (this.gameType == Game.GAME_TYPE_NETWORK) {
			// 如果不该我下，就什么都不做
			if (this.curPieceColor == 0)
				return;
			if (this.curPieceColor == Game.BLACK && !this.player1.equals(Context.account))
				return;
			if (this.curPieceColor == Game.WHITE && !this.player2.equals(Context.account))
				return;
			// 否则检查落子是否有效
			int x = e.getX();
			int y = e.getY();
			int[] r = positionToRowColumn(x, y);
			if (r == null)
				return;
			int row = r[0], col = r[1];
			if (this.state[row][col] != 0)
				return;
			// 如果无效，什么都不做
			if (this.state[row][col] != 0)
				return;
			// 否则，记录棋盘状态变化
			this.state[row][col] = this.curPieceColor;
			if (this.curPieceColor == Game.BLACK)
				this.curPieceColor = Game.WHITE;
			else
				this.curPieceColor = Game.BLACK;

			// 并向服务器发送落子信息
			Context.send("piece;" + row + ";" + col);
			// 更新界面
			this.repaint();

			return;
		}

		int x = e.getX(); // 当鼠标按钮按下并抬起时鼠标在窗口中的位置
		int y = e.getY();

		// 确定真正画棋盘的位置，但是不在这里画，因为有可能会被虚拟机擦除
		int[] r = positionToRowColumn(x, y);
		if (r == null)
			return;
		// 取得落子位置对应棋盘的行列
		int row = r[0], col = r[1];
		if (this.state[row][col] != 0)
			return;

		// 将棋盘状态数组的第row行第col列上落黑子
		this.state[row][col] = curPieceColor;
		this.lastRow = row;
		this.lastColumn = col;

		// 应该在这里判断输赢结果
		int result = judge.doJudge(this.state);
		if (result == BLACK) {
			JOptionPane.showMessageDialog(this, "黑方胜利！");
			saveResult(BLACK);
		} else if (result == WHITE) {
			JOptionPane.showMessageDialog(this, "白方胜利！");
			saveResult(WHITE);
		} else {
			// 如果是人机对弈，黑方落子后立刻由机器落子,然后再判断一次输赢,然后又该黑方落子
			if (this.gameType == Game.GAME_TYPE_MANMACHINE) {
				int[] robot_result = robot.think(state, row, col);
				this.state[robot_result[0]][robot_result[1]] = Game.WHITE;
				this.lastRow = robot_result[0];
				this.lastColumn = robot_result[1];

				result = judge.doJudge(this.state);
				if (result == BLACK) {
					JOptionPane.showMessageDialog(this, "黑方胜利！");
					saveResult(BLACK);
				} else if (result == WHITE) {
					JOptionPane.showMessageDialog(this, "白方胜利！");
					saveResult(WHITE);
				}
			} else {
				// 切换落子颜色
				if (curPieceColor == BLACK)
					curPieceColor = WHITE;
				else
					curPieceColor = BLACK;
			}
		}
		// 该函数让虚拟机去调用paint函数
		this.repaint();

	}

	// 当鼠标按钮按下未抬起的时候该函数被虚拟机调用
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	// 当鼠标按钮按下停会再抬起的时候该函数被虚拟机调用
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	// 当鼠标位置进入到窗口范围时候该函数被虚拟机调用
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	// 当鼠标位置离开进入到窗口范围时候该函数被虚拟机调用
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	// 点击菜单时，由虚拟机调用该函数
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		// 判断哪个菜单被点击了
		if (e.getSource() == this.menuItemExit) {// 退出菜单被点击了
			this.dispose();
		} else if (e.getSource() == this.menuItemManMan) {
			int gameType = Game.GAME_TYPE_MANMAN;

			String player1 = JOptionPane.showInputDialog("请输入第一个玩家的名称");
			String player2 = JOptionPane.showInputDialog("请输入第二个玩家的名称");

			reset(gameType, player1, player2);
		} else if (e.getSource() == this.menuItemManMachine) {
			int gameType = Game.GAME_TYPE_MANMACHINE;

			String player1 = JOptionPane.showInputDialog("请输入第一个玩家的名称");
			String player2 = "robot";

			reset(gameType, player1, player2);
		} else if (e.getSource() == this.menuItemNetwork) {
			LoginDialog dlg = new LoginDialog();
			dlg.setModal(true);
			dlg.setVisible(true);
		} else if (e.getSource() == this.menuItemGameLoggy) {
			// 检查是否登录
			if (Context.account == null) {
				JOptionPane.showMessageDialog(this, "必须登录才能进入大厅");
				return;
			}

			// 显示游戏大厅窗口
			Context.loggyDialog = new GameLoggyDialog();
			Context.loggyDialog.setModal(true);
			Context.loggyDialog.setVisible(true);
		} else if (e.getSource() == this.menuItemUndo) {
			// 悔棋操作，每次落子的时候将落子位置记录在lastRow和lastColumn中，表示最后一次落子的位置
			if (this.lastRow < 0 || this.lastColumn < 0)
				return;
			// 将棋盘上最后一次落子的位置的状态改为0，表示没有落子
			this.state[lastRow][lastColumn] = 0;
			// 因为只悔棋一步，将最后落子位置改为无效（-1）
			this.lastRow = -1;
			this.lastColumn = -1;
			// 重画棋盘和棋子
			this.repaint();
		} else if (e.getSource() == this.menuItemSaveGame) {
			// 将棋盘状态转换为字符串数组，每行对应棋盘上的一行
			String[] stateStr = stateToString();
			String firstRow = this.curPieceColor + "," + this.gameType + "," + this.player1 + "," + this.player2 + ","
					+ this.lastRow + "," + this.lastColumn;
			// 打开文件，将字符串数组依次写入
			try {
				saveToFile("d:\\save.txt", new String[] { firstRow }, false);
				saveToFile("d:\\save.txt", stateStr, true);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "存盘失败，原因：" + ex.getMessage());
			}

		} else if (e.getSource() == this.menuItemLoadGame) {
			// 读取文件
			String[] lines = null;
			try {
				lines = loadFromFile("d:\\save.txt");
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "读取文件失败:" + ex.getMessage());
				return;
			}
			// 第一行是当前棋局的基本信息
			String info = lines[0];
			String[] strs = info.split(",");
			this.curPieceColor = Integer.parseInt(strs[0]);
			this.gameType = Integer.parseInt(strs[1]);
			this.player1 = strs[2];
			this.player2 = strs[3];
			this.lastRow = Integer.parseInt(strs[4]);
			this.lastColumn = Integer.parseInt(strs[5]);
			// 从第二行开始是棋盘落子状态
			for (int i = 1; i < lines.length; i++) {
				String[] columns = lines[i].split(",");
				for (int j = 0; j < columns.length; j++) {
					this.state[i - 1][j] = Integer.parseInt(columns[j]);
				}
			}
			// 刷新界面
			this.repaint();
		} else if (e.getSource() == this.menuItemHistory) {
			// 读取历史战绩
			String[] strs = null;
			try {
				strs = this.loadFromFile("d:\\result.txt");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
			}
			// 将读取到的字符串数组连接成一个完整字符串
			StringBuffer str = new StringBuffer();
			for (String s : strs) {
				if (!str.toString().equals(""))
					str.append("\r\n");
				str.append(s);
			}

			// 显示
			JOptionPane.showMessageDialog(this, str.toString());

		}

	}

	private String[] loadFromFile(String fullfilename) throws Exception {
		File file = new File(fullfilename);
		FileReader reader = null;
		BufferedReader buf = null;
		try {
			reader = new FileReader(file);
			buf = new BufferedReader(reader);
			String s = null;
			int len = 0;
			StringBuffer temp = new StringBuffer();
			while ((s = buf.readLine()) != null) {
				len += 1;
				if (!temp.toString().equals(""))
					temp.append(";");
				temp.append(s);
			}
			return temp.toString().split(";");
		} finally {
			buf.close();
		}
	}

	/**
	 * 将二维棋盘状态数组转换为一维字符串数组，棋盘的每行转为一个字符串，每列用逗号分开
	 * 
	 * @return
	 */
	private String[] stateToString() {
		String[] strs = new String[19];
		for (int i = 0; i < 19; i++) {
			StringBuffer str = new StringBuffer();
			for (int j = 0; j < 19; j++) {
				if (!str.toString().equals(""))
					str.append(",");
				str.append(state[i][j]);
			}
			strs[i] = str.toString();
		}
		return strs;
	}

	/**
	 * 保存战绩到文件
	 * 
	 * @param winColor
	 */
	private void saveResult(int winColor) {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
		Date now = new Date();
		String info = fmt.format(now) + " " + this.player1 + " VS " + this.player2 + " :结果"
				+ (winColor == 1 ? this.player1 : this.player2) + "胜利";
		try {
			this.saveToFile("d:\\result.txt", new String[] { info }, true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 保存文件
	 * 
	 * @param fullfilename 文件名
	 * @param contents     字符串数组，每个字符串一行
	 * @param append       是添加模式写，还是覆盖赋值写
	 * @throws Exception
	 */
	private void saveToFile(String fullfilename, String[] contents, boolean append) throws Exception {
		File file = new File(fullfilename);
		FileWriter writer = null;
		BufferedWriter buf = null;
		try {
			writer = new FileWriter(file, append);
			buf = new BufferedWriter(writer);
			for (String str : contents) {
				buf.write(str);
				buf.newLine();
			}
		} finally {
			buf.close();
		}
	}

	// 窗口被打开时由虚拟机调用
	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	// 窗口被关闭时（还没有关闭掉）由虚拟机调用
	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		int r = JOptionPane.showConfirmDialog(this, "退出游戏，确认？", "提醒", JOptionPane.OK_CANCEL_OPTION);
		if (r == JOptionPane.CANCEL_OPTION) {
			this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		} else
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	// 窗口被关闭以后由虚拟机调用
	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	// 窗口最小化时候由虚拟机调用
	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	// 窗口从最小化恢复时候由虚拟机调用
	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	// 窗口被点击激活时候由虚拟机调用
	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	// 窗口失去焦点时候由虚拟机调用
	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

}
