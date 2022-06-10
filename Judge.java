package version2;

/**
 * 该类用于判断五子棋的输赢
 * @author RoomLess
 *
 */
public class Judge {
	/**
	 * 判断输赢，1表示黑方胜利，2表示白方胜利，0表示对弈中
	 * 
	 * @return
	 */
	public int doJudge(int[][] state) {
		int r = 0;
		for (int i = 0; i < 19; i++) {
			for (int j = 0; j < 19; j++) {
				r = judgeHorizontal(state, i, j); // 判断棋盘第i行第j列在水平方向是否连接出五个子
				if (r != 0)
					return r;
				r = judgeVertical(state, i, j); // 判断棋盘第i行第j列在垂直方向是否连接出五个子
				if (r != 0)
					return r;
				r = judgeLeftLean(state, i, j); // 判断棋盘第i行第j列在左上到右下方向是否连接出五个子
				if (r != 0)
					return r;
				r = judgeRightLean(state, i, j); // 判断棋盘第i行第j列在左下到右上方向是否连接出五个子
				if (r != 0)
					return r;
			}
		}
		return 0;
	}

	private int judgeRightLean(int[][] state, int row, int col) {
		int color = state[row][col];
		if (color == 0)
			return 0; // 这地方没落子

		int n1 = 0, t = 1;
		while (row + t < 19 && col - t >= 0) {
			if (state[row + t][col - t] == color)
				n1 += 1;
			t += 1;
		}

		int n2 = 0;
		t = 1;
		while (row - t >= 0 && col + t < 19) {
			if (state[row - t][col + t] == color)
				n2 += 1;
			t += 1;
		}
		return (n1 + n2 + 1 == 5) ? color : 0;
	}

	private int judgeLeftLean(int[][] state, int row, int col) {
		int color = state[row][col];
		if (color == 0)
			return 0; // 这地方没落子

		int n1 = 0, t = 1;
		while (row - t >= 0 && col - t >= 0) {
			if (state[row - t][col - t] == color)
				n1 += 1;
			t += 1;
		}
		int n2 = 0;
		t = 1;
		while (row + t < 19 && col + t < 19) {
			if (state[row + t][col + t] == color)
				n2 += 1;
			t += 1;
		}
		return (n1 + n2 + 1 == 5) ? color : 0;

	}

	private int judgeVertical(int[][] state, int row, int col) {
		int color = state[row][col];
		if (color == 0)
			return 0; // 这地方没落子

		int n1 = 0;
		for (int t = row - 1; t >= 0; t--) {
			if (state[t][col] == color)
				n1 += 1;
		}

		int n2 = 0;
		for (int t = row + 1; t < 19; t++) {
			if (state[t][col] == color)
				n2 += 1;
		}

		if (n1 + n2 + 1 == 5)
			return color;
		else
			return 0;
	}

	private int judgeHorizontal(int[][] state, int row, int col) {
		int color = state[row][col];
		if (color == 0)
			return 0; // 这地方没落子

		// 左边同颜色的子
		int n1 = 0;
		for (int t = col - 1; t >= 0; t--) {
			if (state[row][t] == color)
				n1 += 1;
		}
		
		// 右边同颜色的子
		int n2 = 0;
		for (int t = col + 1; t < 19; t++) {
			if (state[row][t] == color)
				n2 += 1;
		}
		
		// 加起来是否到5个
		if (n1 + n2 + 1 == 5)
			return color;
		return 0;
	}

}
