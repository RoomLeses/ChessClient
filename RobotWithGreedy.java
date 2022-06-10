package version2;

/**
 * 对弈机器人
 * 
 * @author Administrator
 *
 */
public class RobotWithGreedy {

	/**
	 * 机器人思考（为简化计算，我们假设机器人总是白方）
	 * 
	 * @param state 棋盘状态
	 * @param row   对手最后落子位置
	 * @param col   对手最后落子位置
	 * @return 包含决策结果，落子的行与列
	 */
	public int[] think(int[][] state, int row, int col) {
		boolean r = false;

		// 先判断自己方是否有地方一旦落子就可以连为五子；
		for (int i = 0; i < 19; i++) {
			for (int j = 0; j < 19; j++) {
				if (state[i][j] != 0)
					continue;
				r = check(state, i, j, Game.WHITE, 5);
				if (r)
					return new int[] { i, j };
			}
		}

		// 再判断对方是否有同样情况
		for (int i = 0; i < 19; i++) {
			for (int j = 0; j < 19; j++) {
				if (state[i][j] != 0)
					continue;
				r = check(state, i, j, Game.BLACK, 5);
				if (r)
					return new int[] { i, j };
			}
		}

		// 再判断对方是否有地方会连接成四子
		for (int i = 0; i < 19; i++) {
			for (int j = 0; j < 19; j++) {
				if (state[i][j] != 0)
					continue;
				r = check(state, i, j, Game.BLACK, 4);
				if (r)
					return new int[] { i, j };
			}
		}

		// 再判断对方是否有地方连接成三子
		for (int i = 0; i < 19; i++) {
			for (int j = 0; j < 19; j++) {
				if (state[i][j] != 0)
					continue;
				r = check(state, i, j, Game.BLACK, 3);
				if (r)
					return new int[] { i, j };
			}
		}

		// 再判断自己是否有地方会连接成四子
		for (int i = 0; i < 19; i++) {
			for (int j = 0; j < 19; j++) {
				if (state[i][j] != 0)
					continue;
				r = check(state, i, j, Game.WHITE, 4);
				if (r)
					return new int[] { i, j };
			}
		}

		// 再判断自己是否有地方连接成三子
		for (int i = 0; i < 19; i++) {
			for (int j = 0; j < 19; j++) {
				if (state[i][j] != 0)
					continue;
				r = check(state, i, j, Game.WHITE, 3);
				if (r)
					return new int[] { i, j };
			}
		}

		// 再判断自己是否有地方会连接成两子
		for (int i = 0; i < 19; i++) {
			for (int j = 0; j < 19; j++) {
				if (state[i][j] != 0)
					continue;
				r = check(state, i, j, Game.WHITE, 2);
				if (r)
					return new int[] { i, j };
			}
		}

		// 否则随机落子
		int x = (int) (Math.random() * 18);
		int y = (int) (Math.random() * 18);
		while (state[x][y] != 0) {
			x = (int) (Math.random() * 18);
			y = (int) (Math.random() * 18);
		}
		return new int[] { x, y };
	}

	/**
	 * 该函数是实现贪婪机器人算法的通用型函数 它表示在棋盘状态state的第row行第col列落颜色是color的子话，能否让color子连接成count个
	 * 
	 * @param state 棋盘状态
	 * @param row   检查行
	 * @param col   检查列
	 * @param color 检查颜色
	 * @param count
	 * @return
	 */
	private boolean check(int[][] state, int row, int col, int color, int count) {

		// 检查row，col位置左边，与color颜色一样的子的个数
		int n1 = 0;
		int pos = col - 1;
		while (pos >= 0 && state[row][pos--] == color)
			n1 += 1;

		// 检查row，col位置右边，与color颜色一样的子的个数
		int n2 = 0;
		pos = col + 1;
		while (pos < state[row].length && state[row][pos++] == color)
			n2 += 1;

		// 计算row，col水平方向可以连成的color颜色子个数
		int n = n1 + n2 + 1;
		if (n == count)
			return true;

		// 检查row，col位置上方，与color颜色一样的子的个数
		n1 = 0;
		pos = row - 1;
		while (pos >= 0 && state[pos--][col] == color)
			n1 += 1;

		// 检查row，col位置下方，与color颜色一样的子的个数
		n2 = 0;
		pos = row + 1;
		while (pos < state.length && state[pos++][col] == color)
			n2 += 1;

		// 计算row，col垂直方向可以连成的color颜色子个数
		n = n1 + n2 + 1;
		if (n == count)
			return true;

		// 检查row，col位置左上方，与color颜色一样的子的个数
		n1 = 0;
		int pos1 = row - 1, pos2 = col - 1;
		while (pos1 >= 0 && pos2 >= 0 && state[pos1--][pos2--] == color)
			n1 += 1;

		// 检查row，col位置右下方，与color颜色一样的子的个数
		n2 = 0;
		pos1 = row + 1;
		pos2 = col + 1;
		while (pos1 < state.length && pos2 < state[row].length && state[pos1++][pos2++] == color)
			n2 += 1;

		// 计算row，col位置的左上到右下方向可以连成的color颜色子个数
		n = n1 + n2 + 1;
		if (n == count)
			return true;

		// 检查row，col位置右上方，与color颜色一样的子的个数
		n1 = 0;
		pos1 = row - 1;
		pos2 = col + 1;
		while (pos1 >= 0 && pos2 < state[row].length && state[pos1--][pos2++] == color)
			n1 += 1;

		// 检查row，col位置左下方，与color颜色一样的子的个数
		n2 = 0;
		pos1 = row + 1;
		pos2 = col - 1;
		while (pos1 < state.length && pos2 >= 0 && state[pos1++][pos2--] == color)
			n2 += 1;

		// 计算row，col位置的左下到右上方向可以连成的color颜色子个数
		n = n1 + n2 + 1;
		if (n == count)
			return true;

		return false;

	}
}
