<template>
  <div class="p-4">
    <div class="mb-6">
      <h2 class="text-[clamp(1.5rem,3vw,2.5rem)] font-bold text-gray-800">系统设置</h2>
      <p class="text-gray-600">管理系统参数和用户配置</p>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-4 gap-6">
      <div class="card lg:col-span-1 p-0">
        <div class="space-y-1 p-3">
          <button @click="activeTab = 'profile'" :class="{'bg-primary/10 text-primary': activeTab === 'profile'}" class="sidebar-item w-full text-left">
            <i class="fa fa-user-circle w-5 text-center" :class="activeTab === 'profile' ? 'text-primary' : 'text-gray-500'"></i>
            <span>个人设置</span>
          </button>
          <button @click="activeTab = 'users'" :class="{'bg-primary/10 text-primary': activeTab === 'users'}" class="sidebar-item w-full text-left">
            <i class="fa fa-users w-5 text-center" :class="activeTab === 'users' ? 'text-primary' : 'text-gray-500'"></i>
            <span>用户管理</span>
          </button>
          <button @click="activeTab = 'roles'" :class="{'bg-primary/10 text-primary': activeTab === 'roles'}" class="sidebar-item w-full text-left">
            <i class="fa fa-shield w-5 text-center" :class="activeTab === 'roles' ? 'text-primary' : 'text-gray-500'"></i>
            <span>角色权限</span>
          </button>
          <button @click="activeTab = 'notifications'" :class="{'bg-primary/10 text-primary': activeTab === 'notifications'}" class="sidebar-item w-full text-left">
            <i class="fa fa-bell w-5 text-center" :class="activeTab === 'notifications' ? 'text-primary' : 'text-gray-500'"></i>
            <span>通知设置</span>
          </button>
          <button @click="activeTab = 'warnings'" :class="{'bg-primary/10 text-primary': activeTab === 'warnings'}" class="sidebar-item w-full text-left">
            <i class="fa fa-exclamation-triangle w-5 text-center" :class="activeTab === 'warnings' ? 'text-primary' : 'text-gray-500'"></i>
            <span>预警规则</span>
          </button>
          <button @click="activeTab = 'rules'" :class="{'bg-primary/10 text-primary': activeTab === 'rules'}" class="sidebar-item w-full text-left">
            <i class="fa fa-balance-scale w-5 text-center" :class="activeTab === 'rules' ? 'text-primary' : 'text-gray-500'"></i>
            <span>法规库管理</span>
          </button>
          <button @click="activeTab = 'params'" :class="{'bg-primary/10 text-primary': activeTab === 'params'}" class="sidebar-item w-full text-left">
            <i class="fa fa-sliders w-5 text-center" :class="activeTab === 'params' ? 'text-primary' : 'text-gray-500'"></i>
            <span>系统参数</span>
          </button>
          <button @click="activeTab = 'data'" :class="{'bg-primary/10 text-primary': activeTab === 'data'}" class="sidebar-item w-full text-left">
            <i class="fa fa-database w-5 text-center" :class="activeTab === 'data' ? 'text-primary' : 'text-gray-500'"></i>
            <span>数据管理</span>
          </button>
          <button @click="activeTab = 'logs'" :class="{'bg-primary/10 text-primary': activeTab === 'logs'}" class="sidebar-item w-full text-left">
            <i class="fa fa-history w-5 text-center" :class="activeTab === 'logs' ? 'text-primary' : 'text-gray-500'"></i>
            <span>操作日志</span>
          </button>
        </div>
      </div>

      <div class="card lg:col-span-3">
        <div v-if="activeTab === 'profile'">
          <h3 class="font-bold text-gray-800 mb-6">个人设置</h3>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div class="md:col-span-1 flex flex-col items-center">
              <img src="https://picsum.photos/id/1005/128/128" alt="User Avatar" class="w-32 h-32 rounded-full mb-4 object-cover">
              <button class="btn btn-secondary text-sm">上传新头像</button>
            </div>
            <div class="md:col-span-2 space-y-4">
              <div><label class="block text-sm font-medium text-gray-700">姓名</label><input type="text" value="克拉玛依交警" class="mt-1 block w-full input"></div>
              <div><label class="block text-sm font-medium text-gray-700">邮箱地址</label><input type="email" value="k-police@example.com" class="mt-1 block w-full input"></div>
              <div><label class="block text-sm font-medium text-gray-700">手机号</label><input type="tel" value="138****1234" class="mt-1 block w-full input"></div>
            </div>
          </div>
          <hr class="my-6">
          <div>
            <h4 class="font-medium text-gray-700 mb-4">修改密码</h4>
            <div class="space-y-4">
              <div><label class="block text-sm font-medium text-gray-700">当前密码</label><input type="password" class="mt-1 block w-full input"></div>
              <div><label class="block text-sm font-medium text-gray-700">新密码</label><input type="password" class="mt-1 block w-full input"></div>
              <div><label class="block text-sm font-medium text-gray-700">确认新密码</label><input type="password" class="mt-1 block w-full input"></div>
            </div>
          </div>
          <div class="flex justify-end gap-3 mt-6"><button class="btn btn-primary">保存更改</button></div>
        </div>
        <div v-if="activeTab === 'users'">
          <h3 class="font-bold text-gray-800 mb-6">用户管理</h3>
          <div class="flex flex-col md:flex-row justify-between items-center mb-4 gap-3">
            <div class="relative w-full md:w-auto">
              <i class="fa fa-search absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></i>
              <input type="text" placeholder="搜索用户..." class="input pl-10 w-full">
            </div>
            <button class="btn btn-primary w-full md:w-auto"><i class="fa fa-plus mr-1"></i> 添加用户</button>
          </div>
          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50"><tr><th class="px-6 py-3 text-left">用户</th><th class="px-6 py-3 text-left">角色</th><th class="px-6 py-3 text-left">状态</th><th class="px-6 py-3 text-left">上次登录</th><th class="px-6 py-3 text-right">操作</th></tr></thead>
              <tbody class="divide-y">
              <tr><td class="px-6 py-4"><div class="flex items-center gap-3"><img src="https://picsum.photos/id/1025/40/40" class="w-10 h-10 rounded-full object-cover"><div><div class="font-medium">李四</div><div class="text-sm text-gray-500">lisi@example.com</div></div></div></td><td class="px-6 py-4">交通执法员</td><td class="px-6 py-4"><span class="badge bg-green-100 text-green-800">活跃</span></td><td class="px-6 py-4">2025-06-09 14:20</td><td class="px-6 py-4 text-right"><button class="text-primary mr-3">编辑</button><button class="text-danger">禁用</button></td></tr>
              <tr><td class="px-6 py-4"><div class="flex items-center gap-3"><img src="https://picsum.photos/id/1026/40/40" class="w-10 h-10 rounded-full object-cover"><div><div class="font-medium">王五</div><div class="text-sm text-gray-500">wangwu@example.com</div></div></div></td><td class="px-6 py-4">数据分析师</td><td class="px-6 py-4"><span class="badge bg-gray-200 text-gray-600">已禁用</span></td><td class="px-6 py-4">2025-05-21 10:05</td><td class="px-6 py-4 text-right"><button class="text-primary mr-3">编辑</button><button class="text-success">启用</button></td></tr>
              </tbody>
            </table>
          </div>
        </div>
        <div v-if="activeTab === 'roles'">
          <h3 class="font-bold text-gray-800 mb-6">角色权限管理</h3>
          <div class="flex justify-between items-center mb-4">
            <div><h4 class="font-medium text-gray-700">系统角色列表</h4><p class="text-sm text-gray-500">管理不同角色的系统访问权限</p></div>
            <button class="btn btn-primary"><i class="fa fa-plus mr-1"></i> 添加角色</button>
          </div>
          <div class="overflow-x-auto mb-6">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50"><tr><th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">角色名称</th><th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">成员数</th><th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">描述</th><th scope="col" class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">操作</th></tr></thead>
              <tbody class="bg-white divide-y divide-gray-200">
              <tr class="hover:bg-gray-50 transition-colors"><td class="px-6 py-4 whitespace-nowrap"><div class="font-medium text-gray-900">系统管理员</div></td><td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">5</td><td class="px-6 py-4 text-sm text-gray-500">拥有系统所有权限，可管理所有功能</td><td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium"><button class="text-primary hover:text-primary/80 mr-3">编辑</button><button class="text-gray-500 hover:text-gray-700">禁用</button></td></tr>
              <tr class="hover:bg-gray-50 transition-colors"><td class="px-6 py-4 whitespace-nowrap"><div class="font-medium text-gray-900">交通执法员</div></td><td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">42</td><td class="px-6 py-4 text-sm text-gray-500">可查看和处理违法记录，管理设备</td><td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium"><button class="text-primary hover:text-primary/80 mr-3">编辑</button><button class="text-gray-500 hover:text-gray-700">禁用</button></td></tr>
              <tr class="hover:bg-gray-50 transition-colors"><td class="px-6 py-4 whitespace-nowrap"><div class="font-medium text-gray-900">数据分析师</div></td><td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">8</td><td class="px-6 py-4 text-sm text-gray-500">可查看统计报表，无管理权限</td><td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium"><button class="text-primary hover:text-primary/80 mr-3">编辑</button><button class="text-gray-500 hover:text-gray-700">禁用</button></td></tr>
              <tr class="hover:bg-gray-50 transition-colors"><td class="px-6 py-4 whitespace-nowrap"><div class="font-medium text-gray-900">设备维护员</div></td><td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">12</td><td class="px-6 py-4 text-sm text-gray-500">可管理监控设备，查看设备状态</td><td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium"><button class="text-primary hover:text-primary/80 mr-3">编辑</button><button class="text-gray-500 hover:text-gray-700">禁用</button></td></tr>
              </tbody>
            </table>
          </div>
          <h4 class="font-medium text-gray-700 mb-4">权限分配</h4>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
            <div class="border border-gray-200 rounded-lg p-4"><h5 class="font-medium text-gray-800 mb-3">系统功能权限</h5><div class="space-y-2"><label class="flex items-center gap-2"><input type="checkbox" class="rounded border-gray-300 text-primary focus:ring-primary" checked><span>仪表盘查看</span></label><label class="flex items-center gap-2"><input type="checkbox" class="rounded border-gray-300 text-primary focus:ring-primary" checked><span>违法记录管理</span></label><label class="flex items-center gap-2"><input type="checkbox" class="rounded border-gray-300 text-primary focus:ring-primary"><span>实时监控查看</span></label><label class="flex items-center gap-2"><input type="checkbox" class="rounded border-gray-300 text-primary focus:ring-primary" checked><span>统计分析查看</span></label><label class="flex items-center gap-2"><input type="checkbox" class="rounded border-gray-300 text-primary focus:ring-primary"><span>设备管理</span></label></div></div>
            <div class="border border-gray-200 rounded-lg p-4"><h5 class="font-medium text-gray-800 mb-3">数据操作权限</h5><div class="space-y-2"><label class="flex items-center gap-2"><input type="checkbox" class="rounded border-gray-300 text-primary focus:ring-primary"><span>添加/编辑违法记录</span></label><label class="flex items-center gap-2"><input type="checkbox" class="rounded border-gray-300 text-primary focus:ring-primary" checked><span>处理违法记录</span></label><label class="flex items-center gap-2"><input type="checkbox" class="rounded border-gray-300 text-primary focus:ring-primary"><span>删除违法记录</span></label><label class="flex items-center gap-2"><input type="checkbox" class="rounded border-gray-300 text-primary focus:ring-primary"><span>添加/删除设备</span></label><label class="flex items-center gap-2"><input type="checkbox" class="rounded border-gray-300 text-primary focus:ring-primary"><span>修改系统设置</span></label></div></div>
          </div>
          <div class="flex justify-end gap-3"><button class="btn btn-secondary">取消</button><button class="btn btn-primary">保存设置</button></div>
        </div>
        <div v-if="activeTab === 'notifications'">
          <h3 class="font-bold text-gray-800 mb-6">通知设置</h3>
          <div class="space-y-6">
            <div><h4 class="font-medium text-gray-700">系统内通知</h4><p class="text-sm text-gray-500 mb-3">选择您想在系统仪表盘上看到的通知类型。</p><div class="space-y-2"><label class="flex items-center gap-2"><input type="checkbox" class="rounded" checked><span>出现一级预警时</span></label><label class="flex items-center gap-2"><input type="checkbox" class="rounded" checked><span>收到新任务指派时</span></label><label class="flex items-center gap-2"><input type="checkbox" class="rounded"><span>系统发布重要公告时</span></label></div></div>
            <div><h4 class="font-medium text-gray-700">邮件通知</h4><p class="text-sm text-gray-500 mb-3">选择您希望通过邮件接收的事件通知。</p><div class="space-y-2"><label class="flex items-center gap-2"><input type="checkbox" class="rounded" checked><span>每日安全报告</span></label><label class="flex items-center gap-2"><input type="checkbox" class="rounded"><span>每周数据摘要</span></label><label class="flex items-center gap-2"><input type="checkbox" class="rounded" checked><span>密码或安全设置被更改时</span></label></div></div>
          </div>
          <div class="flex justify-end gap-3 mt-6"><button class="btn btn-primary">保存设置</button></div>
        </div>
        <div v-if="activeTab === 'warnings'"><h3 class="font-bold text-gray-800 mb-6">预警规则管理</h3><div class="space-y-4"><div class="p-4 border rounded-lg"><h4 class="font-medium">一级预警 <span class="badge bg-danger text-white">即时推送</span></h4><p class="text-sm text-gray-500 my-2">当违法行为满足以下任一条件时，触发最高级别预警。</p><p><code>(违法类型 = 闯红灯 OR 逆行) AND (置信度 >= 0.8)</code></p></div><div class="p-4 border rounded-lg"><h4 class="font-medium">二级预警 <span class="badge bg-warning text-white">每日汇总</span></h4><p class="text-sm text-gray-500 my-2">当违法行为满足以下条件时，触发二级预警。</p><p><code>(违法类型 = 超速) AND (置信度 >= 0.6)</code></p></div></div><div class="text-right mt-6"><button class="btn btn-primary">编辑规则</button></div></div>
        <div v-if="activeTab === 'rules'"><h3 class="font-bold text-gray-800 mb-6">法规库管理</h3><p class="text-sm text-gray-500 mb-4">维护违法行为与交通法规的对应关系、处罚标准。</p><table class="min-w-full divide-y divide-gray-200"><thead class="bg-gray-50"><tr><th class="px-6 py-3 text-left">违法类型</th><th class="px-6 py-3 text-left">对应条款</th><th class="px-6 py-3 text-left">罚款(元)</th><th class="px-6 py-3 text-left">记分</th><th class="px-6 py-3 text-right">操作</th></tr></thead><tbody class="divide-y"><tr><td class="px-6 py-4">闯红灯</td><td class="px-6 py-4">第26条, 第90条</td><td class="px-6 py-4">200</td><td class="px-6 py-4">6</td><td class="px-6 py-4 text-right"><button class="text-primary">编辑</button></td></tr><tr><td class="px-6 py-4">超速20%</td><td class="px-6 py-4">第42条, 第99条</td><td class="px-6 py-4">500</td><td class="px-6 py-4">3</td><td class="px-6 py-4 text-right"><button class="text-primary">编辑</button></td></tr><tr><td class="px-6 py-4">逆行</td><td class="px-6 py-4">第35条, 第90条</td><td class="px-6 py-4">200</td><td class="px-6 py-4">3</td><td class="px-6 py-4 text-right"><button class="text-primary">编辑</button></td></tr></tbody></table><div class="text-right mt-6"><button class="btn btn-primary">添加新条款</button></div></div>
        <div v-if="activeTab === 'params'"><h3 class="font-bold text-gray-800 mb-6">系统参数</h3><div class="space-y-4"><div><label class="block text-sm font-medium text-gray-700">系统名称</label><input type="text" value="城市交通智能执法平台" class="mt-1 block w-full input"></div><div><label class="block text-sm font-medium text-gray-700">会话超时时间 (分钟)</label><input type="number" value="30" class="mt-1 block w-full input"></div><div><label class="block text-sm font-medium text-gray-700">数据保留策略 (天)</label><input type="number" value="365" class="mt-1 block w-full input"><p class="text-xs text-gray-500 mt-1">系统将自动清除超过此天数的历史操作日志和违法记录。</p></div></div><div class="flex justify-end gap-3 mt-6"><button class="btn btn-primary">保存参数</button></div></div>
        <div v-if="activeTab === 'data'"><h3 class="font-bold text-gray-800 mb-6">数据管理</h3><div class="space-y-6"><div class="p-4 border rounded-lg"><h4 class="font-medium">数据备份</h4><p class="text-sm text-gray-500 my-2">创建系统数据的完整备份。上次备份时间：2025-06-08 02:00:00</p><button class="btn btn-secondary">立即备份</button></div><div class="p-4 border border-warning/50 rounded-lg bg-warning/5"><h4 class="font-medium text-warning">数据恢复</h4><p class="text-sm text-gray-500 my-2">从备份文件恢复数据。注意：此操作将覆盖现有数据且不可逆。</p><input type="file" class="text-sm"><button class="btn btn-warning ml-4">开始恢复</button></div></div></div>
        <div v-if="activeTab === 'logs'"><h3 class="font-bold text-gray-800 mb-6">操作日志</h3><div class="flex flex-col md:flex-row justify-between items-center mb-4 gap-3"><div class="relative w-full md:w-auto"><i class="fa fa-search absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></i><input type="text" placeholder="搜索操作或用户..." class="input pl-10 w-full"></div><input type="date" class="input w-full md:w-auto"></div><div class="overflow-x-auto"><table class="min-w-full divide-y divide-gray-200"><thead class="bg-gray-50"><tr><th class="px-6 py-3 text-left">时间</th><th class="px-6 py-3 text-left">用户</th><th class="px-6 py-3 text-left">IP地址</th><th class="px-6 py-3 text-left">操作详情</th></tr></thead><tbody class="divide-y"><tr><td class="px-6 py-4">2025-06-09 16:10</td><td class="px-6 py-4">张三</td><td class="px-6 py-4">192.168.1.101</td><td class="px-6 py-4">登录系统</td></tr><tr><td class="px-6 py-4">2025-06-09 15:45</td><td class="px-6 py-4">李四</td><td class="px-6 py-4">202.55.10.18</td><td class="px-6 py-4">更新了ID为 "R003" 的角色权限</td></tr><tr><td class="px-6 py-4">2025-06-09 15:30</td><td class="px-6 py-4">李四</td><td class="px-6 py-4">202.55.10.18</td><td class="px-6 py-4">处理了违法记录 "V20250609-0123"</td></tr></tbody></table></div></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
const activeTab = ref('profile');
</script>
