import {
  createWebHistory,
  createRouter
} from 'vue-router'

// 导入API
import LoadMenusApi from '../../../api/LoadMenusApi';

// 导入所需组件（统一路径，无重复导入）
import NotFound from '../routercom/404.vue';

// 导入ElMessage用于提示
import { ElMessage } from 'element-plus';

// 路由规则：合并所有有效路由，去重+规范格式
const routes = [
  // 1. 根路径重定向（优先跳未登录页的首页，符合登录流程）
  {
    path: '/',
    name: 'Home',
    component: () => import('../routercom/home.vue'),
    meta: { title: '首页' },
    children: [
      {
        path: '/home/selectall',
        name: 'HomeSelectAll',
        component: () => import('../routercom/Home-SelectAll.vue'),
        meta: { title: '数据查询' }
      }, {
        path: '/home/trending',
        name: 'trending',
        component: () => import('../routercom/trending.vue'),
        meta:{title:'所有商家'}
      }, {
        path: '/home/AllCategory',
        name: 'AllCategory',
        component: () => import('../routercom/AllCategory.vue'),
        meta: { title: '所有分类' }
      },{
        path:'/home/Product',
        name:'Product',
        component:()=>import('../routercom/Product.vue'),
        meta:{title:'所有商品'}
      },
      {
        path: '/cz',
        name: 'Cz',
        component: () => import('/src/components/routercom/personalcenter/cz.vue'),
        meta: { title: '操作中心', requiresAuth: false },
        children: [
          {
            path: 'favorites',
            name: 'Favorites',
            component: () => import('../routercom/personalcenter/Favorite.vue'),
            meta: { title: '我的收藏' }
          },
          {
            path: 'coupon',
            name: 'Coupon',
            component: () => import('../routercom/personalcenter/coupon.vue'),
            meta: { title: '每日领券' }
          }
        ]
      },{
        path:'/home/DishDetail/:id',
        name:'DishDetail',
        component:()=>import('../routercom/DishDetail.vue'),
        meta:{title:'菜品详情'}
      },{
        path:'/home/cart',
        name:'cart',
        component:()=>import('../routercom/Cart.vue'),
        meta:{title:'购物车'}
      },{
        path:'/home/ShopGoods/:Id',
        name:'ShopGoods',
        component:()=>import('../routercom/ShopGoods.vue'),
        meta:{title:'商家详情'}
      },{
        path:'/home/Settlement',
        name:'Settlement',
        component: () => import('../routercom/Settlement.vue'),
        meta:{title:'商品结算'}
      },{
        path:'/home/PaymentSuccess',
        name:'PaymentSuccess',
        component: () => import('../routercom/PaymentSuccess.vue'),
        meta:{title:'支付成功'}
      }
    ]
  },

  // 2. 登录相关路由（去重，统一命名）
  {
    path: '/login',
    name: 'Login',
    component: () => import('../routercom/login/Login.vue'),
    meta: { title: '用户登录', requiresAuth: false }
  },
  {
    path: '/adminlogin',
    name: 'AdminLogin',
    component: () => import('../routercom/admin/adminlogin.vue'),
    meta: { title: '管理员登录', requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../routercom/register/Register.vue'),
    meta: { title: '管理员登录', requiresAuth: false }
  },
  {
    path: '/forget-password',
    name: 'ForgetPassword',
    component: () => import('../routercom/login/ForgetPassword.vue'),
    meta: { title: '忘记密码', requiresAuth: false }
  },

  // 管理员中心路由
  {
    path: '/adminmain',
    name: 'adminmain',
    component: () => import('../routercom/admin/AdminMain.vue'),
    meta: { title: '管理员中心', requiresAuth: false },
    children:[]
  },

  // 7. 404页面（必须放在最后，拦截所有未匹配路径）
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: NotFound,
    meta: { title: '页面不存在', requiresAuth: false }
  },
  {
    path: '/addshopbyshop',
    name: 'addshopbyshop',
    component: () => import('../routercom/register/addshop.vue'),
    meta: {
      title: '商家入驻',
      requiresAuth: false
    }
  }
];

// 创建路由实例（唯一实例，无重复声明）
const router = createRouter({
  history: createWebHistory(),
  routes,
})

let hasLoadedDynamicRoutes = false

// 3. 路由守卫：确保动态路由加载完成后再放行导航
router.beforeEach(async (to, from) => {
  console.log('===== 路由守卫开始 =====');
  console.log('当前访问路径:', to.path);
  console.log('来源路径:', from.path);
  
  // 模拟获取token（根据你的实际登录逻辑调整）
  const token = localStorage.getItem('token')
  console.log('当前token:', token ? '已存在' : '不存在');
  
  // 白名单：无需登录即可访问的路由
  const whiteList = ['/', '/login', '/adminlogin', '/register', '/forget-password', '/404', '/home/selectall', '/home/trending', '/home/AllCategory', '/home/Product', '/addshopbyshop']
  console.log('白名单:', whiteList);
  
  // 需要登录的页面
  const requireAuthPages = ['/home/cart', '/home/Settlement', '/home/PaymentSuccess', '/cz']
  
  // 检查URL中是否有token参数（支付宝回调）
  if (to.path === '/home/PaymentSuccess') {
    const urlParams = new URLSearchParams(to.fullPath.split('?')[1] || '')
    const tokenFromUrl = urlParams.get('token')
    
    if (tokenFromUrl && !token) {
      console.log('从URL恢复token到localStorage')
      localStorage.setItem('token', tokenFromUrl)
      // 从token中解析用户信息
      try {
        const payload = JSON.parse(atob(tokenFromUrl.split('.')[1]))
        if (payload.sub) {
          localStorage.setItem('userid', payload.sub)
        }
      } catch (error) {
        console.error('解析token失败:', error)
      }
    }
  }
  
  // 检查是否需要加载动态路由
  console.log('动态路由加载状态:', hasLoadedDynamicRoutes ? '已加载' : '未加载');
  
  if (!hasLoadedDynamicRoutes) {
    console.log('需要加载动态路由');
    // 无论是否登录，只要访问adminmain相关路由或已登录，就加载动态路由
    console.log('是否满足加载条件:', to.path.startsWith('/adminmain') || token ? '是' : '否');
    
    if (to.path.startsWith('/adminmain') || token) {
      console.log('开始执行动态路由加载...');
      await loadDynamicRoutes();
      console.log('动态路由加载完成，重新导航到:', to.path);
      return {
        ...to,
        replace: true
      } // 关键：重新触发一次导航，确保新加载的路由能匹配
    }
  } else {
    // 动态路由已加载，检查是否有匹配的路由
    const matchedRoute = router.resolve(to).matched;
    console.log('当前路由匹配情况:', matchedRoute.length > 0 ? '已匹配' : '未匹配');
    if (matchedRoute.length === 0 && to.path.startsWith('/adminmain/')) {
      // 如果没有匹配到路由，但路径是/adminmain/开头，可能是动态路由加载不完整
      console.log('路由未匹配，重新加载动态路由...');
      hasLoadedDynamicRoutes = false; // 重置状态，允许重新加载
      await loadDynamicRoutes();
      console.log('重新加载动态路由完成，重新导航到:', to.path);
      return {
        ...to,
        replace: true
      };
    }
  }
  
  // 1. token没有未登录
  if (!token) {
    console.log('用户未登录');
    // 检查是否在白名单中
    const isWhiteList = whiteList.includes(to.path) || to.path.startsWith('/home/') && !requireAuthPages.includes(to.path);
    console.log('是否在白名单中:', isWhiteList ? '是' : '否');
    
    if (!isWhiteList || to.path.startsWith('/adminmain/') || requireAuthPages.includes(to.path)) { // 没登录，且访问的不是白名单页面 → 跳转到登录页
      console.log('重定向到登录页');
      // 提示用户需要登录
      ElMessage.warning('请先登录');
      return {
        path: '/login',
        query: { redirect: to.fullPath } // 记录重定向路径
      };
    }
    console.log('放行，无需登录');
  } else {
    console.log('用户已登录，放行');
  }
  
  console.log('===== 路由守卫结束 =====');
});

// 2. 封装动态添加路由的函数，返回Promise确保执行完成
const loadDynamicRoutes = async () => {
  if (hasLoadedDynamicRoutes) return // 已加载则直接返回
  try {
    console.log('开始加载动态路由...');
    
    // 尝试调用API加载菜单
    let res;
    try {
      res = await LoadMenusApi.LoadMenusForAdmin();
      console.log("API返回的原始数据", res);
    } catch (apiError) {
      console.error('API调用失败：', apiError);
      // 不使用模拟数据，直接使用空结果
      res = null;
    }
    
    // 验证并提取菜单列表
    let menusList = [];
    if (res && typeof res === 'object') {
      // 先处理可能的嵌套结构（API返回格式：{ data: { menusList: [] } }）
      const data = res.data || {};
      
      // 兼容不同的字段名
      if (Array.isArray(data.menusList)) {
        // 优先使用驼峰命名（menusList）
        menusList = data.menusList;
      } else if (Array.isArray(data.menuslist)) {
        // 兼容小写s（menuslist）
        menusList = data.menuslist;
      } else if (Array.isArray(res.menusList)) {
        // 兼容直接在res下的驼峰命名
        menusList = res.menusList;
      } else if (Array.isArray(res.menuslist)) {
        // 兼容直接在res下的小写s
        menusList = res.menuslist;
      } else if (Array.isArray(res.data)) {
        // 兼容res.data直接是数组的情况
        menusList = res.data;
      } else if (Array.isArray(res)) {
        // 兼容res直接是数组的情况
        menusList = res;
      } else {
        // 尝试其他可能的字段名
        menusList = data.menu || data.list || res.menu || res.list || [];
        if (!Array.isArray(menusList)) {
          menusList = [];
        }
      }
    }
    
    console.log("最终处理后的菜单列表", menusList);
    
    // 不使用任何模拟数据，完全依赖API返回结果
    if (menusList.length === 0) {
      console.log('菜单列表为空，不加载动态路由');
      hasLoadedDynamicRoutes = true; // 标记为已加载，避免重复请求
      return;
    }

    // 构建菜单树结构
    const menuMap = new Map();
    const rootMenus = [];
    
    // 先将所有菜单存入map，兼容不同的字段名
    for (const menu of menusList) {
      const menuId = menu.id || menu.menuId;
      menuMap.set(menuId, { ...menu, children: [] });
    }
    
    // 构建层级关系，兼容不同的字段名
    for (const menu of menusList) {
      const menuId = menu.id || menu.menuId;
      const parentId = menu.parentId;
      const menuName = menu.name || menu.menuName;
      
      // 兼容parentId为1的情况（顶级菜单）
      if (parentId === 0 || parentId === 1) {
        // 顶级菜单
        rootMenus.push(menuMap.get(menuId));
      } else {
        // 子菜单，添加到父菜单的children中
        const parentMenu = menuMap.get(parentId);
        if (parentMenu) {
          parentMenu.children.push(menuMap.get(menuId));
        } else {
          console.log(`找不到父菜单：${parentId}，菜单：${menuId}-${menuName}`);
          // 如果找不到父菜单，将其作为顶级菜单处理
          rootMenus.push(menuMap.get(menuId));
        }
      }
    }
    
    console.log("构建的菜单树结构", rootMenus);
    
    // 递归添加路由
    const addRouteRecursively = (parentRouteName, menus) => {
      for (const menu of menus) {
        // 兼容不同的字段名
        const menuId = menu.id || menu.menuId;
        const menuName = menu.name || menu.menuName;
        const menuPath = menu.path || menu.menuUrl;
        const menuComponent = menu.component;
        
        // 生成唯一的路由名称
        const routeName = menuName || `dynamic_${menuPath?.replace(/\//g, '_') || menuId}`;
        
        console.log(`处理菜单：${menuId}-${menuName}，路径：${menuPath}，组件：${menuComponent}`);
        
        // 确保路径格式正确
        let routePath = menuPath;
        if (routePath) {
          // 对于adminmain下的路由，提取相对路径（去掉/adminmain/前缀）
          if (routePath.startsWith('/adminmain/')) {
            routePath = routePath.replace(/^\/adminmain\//, '');
          } else {
            // 其他情况只去掉开头的/
            routePath = routePath.replace(/^\//, '');
          }
          console.log(`处理后的路由路径：${routePath}`);
        }
        
        if (routePath && menuComponent) {
          // 有路径和组件的菜单，添加为实际路由
          const componentPath = menuComponent.trim();
          console.log(`添加路由：${parentRouteName} -> ${routeName}，路径：${routePath}，组件路径：${componentPath}`);
          
          try {
            // 确保组件路径是相对路径，从src目录开始
            let resolvedComponentPath = componentPath;
            if (!resolvedComponentPath.startsWith('..')) {
              // 如果是绝对路径或从src开始，转换为相对路径
              resolvedComponentPath = `../${resolvedComponentPath}`;
            }
            
            console.log(`准备添加路由：${parentRouteName} -> ${routeName}，路径：${routePath}，最终组件路径：${resolvedComponentPath}`);
            
            // 先检查是否已存在该路由（通过名称或路径）
            const allRoutes = router.getRoutes();
            const existingRoute = allRoutes.find(route => 
              route.name === routeName || 
              (route.path === routePath && route.parent && route.parent.name === parentRouteName)
            );
            if (existingRoute) {
              console.log(`路由已存在：${existingRoute.name} (路径：${existingRoute.path})，跳过添加`);
              continue;
            }
            
            // 使用动态导入添加路由
            const route = {
              path: routePath,
              name: routeName,
              component: () => import(/* @vite-ignore */ `${resolvedComponentPath}`),
              meta: {
                title: menu.title || menuName || menuPath,
                requiresAuth: true
              },
              children: [] // 预留子路由位置
            };
            
            const addedRoute = router.addRoute(parentRouteName, route);
            console.log(`路由添加成功：${routeName}，添加结果：${addedRoute ? '成功' : '失败'}`);
          } catch (e) {
            console.error(`路由添加失败：${routeName}，错误详情：`, e);
            // 打印更详细的错误信息
            if (e.message) {
              console.error(`错误信息：${e.message}`);
            }
            if (e.stack) {
              console.error(`错误堆栈：${e.stack}`);
            }
          }
          
          // 递归添加子菜单
          if (menu.children && menu.children.length > 0) {
            console.log(`递归处理子菜单：${menuId}-${menuName}，子菜单数量：${menu.children.length}`);
            addRouteRecursively(routeName, menu.children);
          }
        } else if (routePath) {
          // 只有路径没有组件的菜单，添加为嵌套路由容器
          console.log(`添加路由容器：${parentRouteName} -> ${routeName}，路径：${routePath}`);
          
          try {
            router.addRoute(parentRouteName, {
              path: routePath,
              name: routeName,
              meta: {
                title: menu.title || menuName || menuPath,
                requiresAuth: true
              },
              children: [] // 子路由容器
            });
            console.log(`路由容器添加成功：${routeName}`);
          } catch (e) {
            console.error(`路由容器添加失败：${routeName}`, e);
          }
          
          // 递归添加子菜单
          if (menu.children && menu.children.length > 0) {
            console.log(`递归处理子菜单：${menuId}-${menuName}，子菜单数量：${menu.children.length}`);
            addRouteRecursively(routeName, menu.children);
          }
        } else if (menu.children && menu.children.length > 0) {
          // 没有路径但有子菜单的，递归处理子菜单
          console.log(`处理无子路径菜单：${menuId}-${menuName}，直接递归子菜单`);
          addRouteRecursively(parentRouteName, menu.children);
        } else {
          console.log(`跳过无效菜单：${menuId}-${menuName}，缺少路径或组件`);
        }
      }
    };
    
    // 从adminmain开始添加所有路由
    console.log(`开始从adminmain添加路由，顶级菜单数量：${rootMenus.length}`);
    addRouteRecursively('adminmain', rootMenus);
    
    hasLoadedDynamicRoutes = true; // 标记为已加载
    
    // 输出所有路由，包括父路由关系
    const allRoutes = router.getRoutes();
    console.log('所有动态路由添加完成，当前路由总数：', allRoutes.length);
    console.log('完整路由列表：');
    allRoutes.forEach(route => {
      const parentName = route.parent ? route.parent.name : 'root';
      console.log(`  ${parentName} -> ${route.name || 'unnamed'}: ${route.path}`);
    });
    
    // 特别检查adminmain的子路由
    const adminmainRoutes = allRoutes.filter(route => route.parent && route.parent.name === 'adminmain');
    console.log('adminmain的子路由：', adminmainRoutes.map(r => ({ name: r.name, path: r.path })));
  } catch (error) {
    console.error('动态路由加载失败：', error);
    // 失败后重置状态，允许下次重试
    hasLoadedDynamicRoutes = false;
  }
};


// 唯一导出（无重复导出）
export default router;