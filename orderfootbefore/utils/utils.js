/**
 * 将数组对象转换为树形结构
 * @param {Array} list - 扁平数组对象
 * @param {string|number} rootValue - 根节点的parentId值（默认null）
 * @param {string} idKey - id字段名（默认'id'）
 * @param {string} parentKey - 父id字段名（默认'parentId'）
 * @param {string} childrenKey - 子节点数组字段名（默认'children'）
 * @returns {Array} 树形结构数组
 */
function arrayToTree1(
	list,
	rootValue = null,
	idKey = 'menuId',
	parentKey = 'parentId',
	childrenKey = 'children'
) {
	// 1. 创建映射表（id -> 节点），方便快速查找
	const map = {};
	// 2. 初始化每个节点的children数组
	list.forEach(item => {
		map[item[idKey]] = {
			...item
		}; // 复制对象，避免修改原数据
		map[item[idKey]][childrenKey] = []; // 初始化子节点数组
	});

	// 3. 遍历节点，挂载到父节点的children中
	const tree = [];
	list.forEach(item => {
		const currentNode = map[item[idKey]];
		const parent_Id = item[parentKey];
		if (parent_Id === rootValue) {
			// 根节点直接加入结果集
			tree.push(currentNode);
		} else {
			// 非根节点，挂载到父节点的children
			const parentNode = map[parent_Id];
			if (parentNode) {
				parentNode[childrenKey].push(currentNode);
			}
		}
	});

	return tree;
}

function a() {
	alert(8)
}
export default {
	arrayToTree,
	a
}
/**
 * 列表转树 + 同级排序
 * @param {Array} list          原始扁平数组
 * @param {*} rootValue         根节点的 parent_id 值，默认 null
 * @param {String} idKey        唯一标识字段，默认 'menu_id'
 * @param {String} parentKey    指向父节点的字段，默认 'parent_id'
 * @param {String} childrenKey  存放子节点的字段，默认 'children'
 * @param {String} sortKey      排序依据字段，空/不传则保持原顺序
 * @returns {Array}             树形结构
 */
function arrayToTree(
	list,
	rootValue = null,
	idKey = 'menu_id',
	parentKey = 'parent_id',
	childrenKey = 'children',
	sortKey = 'sort'
) {
	// 1. 建立 id -> 节点 映射，并初始化 children
	const map = {};
	list.forEach(item => {
		map[item[idKey]] = {
			...item,
			[childrenKey]: []
		};
	});

	// 2. 挂载到父节点
	const tree = [];
	list.forEach(item => {
		const node = map[item[idKey]];
		const pId = item[parentKey];

		if (pId == rootValue) {
			tree.push(node);
		} else {
			const parent = map[pId];
			if (parent) parent[childrenKey].push(node);
		}
	});

	// 3. 递归排序（只排同级）
	function sortChildren(nodes) {
		if (!sortKey) return; // 没传排序字段直接跳过
		nodes.sort((a, b) => a[sortKey] - b[sortKey]);
		nodes.forEach(n => sortChildren(n[childrenKey]));
	}
	sortChildren(tree);

	return tree;
}
