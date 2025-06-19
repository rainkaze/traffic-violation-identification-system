<template>
  <div class="p-4">
    <div class="mb-6">
      <h2 class="text-[clamp(1.5rem,3vw,2.5rem)] font-bold text-gray-800">系统设置</h2>
      <p class="text-gray-600">管理系统参数和用户配置</p>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-4 gap-6">
      <div class="card lg:col-span-1 p-0">
        <div class="space-y-1 p-3">
<!--          <button @click="activeTab = 'profile'" :class="{'bg-primary/10 text-primary': activeTab === 'profile'}" class="sidebar-item w-full text-left">-->
<!--            <i class="fa fa-user-circle w-5 text-center" :class="activeTab === 'profile' ? 'text-primary' : 'text-gray-500'"></i>-->
<!--            <span>个人设置</span>-->
<!--          </button>-->
<!--          <button @click="activeTab = 'users'" :class="{'bg-primary/10 text-primary': activeTab === 'users'}" class="sidebar-item w-full text-left">-->
<!--            <i class="fa fa-users w-5 text-center" :class="activeTab === 'users' ? 'text-primary' : 'text-gray-500'"></i>-->
<!--            <span>用户管理</span>-->
<!--          </button>-->
<!--          <button @click="activeTab = 'roles'" :class="{'bg-primary/10 text-primary': activeTab === 'roles'}" class="sidebar-item w-full text-left">-->
<!--            <i class="fa fa-shield w-5 text-center" :class="activeTab === 'roles' ? 'text-primary' : 'text-gray-500'"></i>-->
<!--            <span>角色权限</span>-->
<!--          </button>-->
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

          <!--           添加/编辑用户弹窗-->
          <div v-if="showUserModal" class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40">
            <div class="bg-white p-6 rounded shadow-lg w-96">
              <h4 class="text-xl mb-4">{{ editingUser.id ? '编辑用户' : '添加用户' }}</h4>

              <form @submit.prevent="saveUser">
                <div class="mb-3">
                  <label class="block mb-1">用户名</label>
                  <input v-model="editingUser.name" type="text" class="input w-full" required />
                </div>
                <div class="mb-3">
                  <label class="block mb-1">邮箱</label>
                  <input v-model="editingUser.email" type="email" class="input w-full" required />
                </div>
                <div class="mb-3">
                  <label class="block mb-1">角色</label>
                  <input v-model="editingUser.role" type="text" class="input w-full" required/>
                </div>
                <div class="mb-3">
                  <label class="block mb-1">状态</label>
                  <select v-model="editingUser.status" class="input w-full" >
                    <option value="active">活跃</option>
                    <option value="disabled">已禁用</option>
                  </select>
                </div>



                <div class="mb-4">
                  <label class="block mb-1">头像</label>
                  <div class="flex items-center gap-3">
                    <img
                      :src="editingUser.avatar"
                      alt="预览"
                      class="w-10 h-10 rounded-full object-cover"
                    />
                    <input
                      type="file"
                      ref="userAvatarInput"
                      @change="handleUserAvatarChange"
                      accept="image/*"
                      class="hidden"
                    />
                    <button
                      type="button"
                      class="btn btn-secondary text-sm"
                      @click="triggerUserAvatarUpload"
                    >
                      选择头像
                    </button>
                  </div>
                </div>
                <div class="flex justify-end gap-3">
                  <button type="button" @click="closeUserModal" class="btn btn-secondary">取消</button>
                  <button type="submit" class="btn btn-primary">{{ editingUser.id ? '保存' : '添加' }}</button>
                </div>











              </form>
            </div>
          </div>
        </div>
















        <div v-if="activeTab === 'roles'">
          <h3 class="font-bold text-gray-800 mb-6">角色权限管理</h3>
          <div class="flex justify-between items-center mb-4">
            <div>
              <h4 class="font-medium text-gray-700">系统角色列表</h4>
              <p class="text-sm text-gray-500">管理不同角色的系统访问权限</p>
            </div>
            <button class="btn btn-primary" @click="openAddRole">
              <i class="fa fa-plus mr-1"></i> 添加角色
            </button>
          </div>
          <div class="overflow-x-auto mb-6">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">角色名称</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">成员数</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">描述</th>
                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">操作</th>
              </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="role in roles" :key="role.id" :class="{ 'opacity-50': role.disabled }" class="hover:bg-gray-50 transition-colors">
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="font-medium text-gray-900">{{ role.name }}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ role.members }}</td>
                <td class="px-6 py-4 text-sm text-gray-500">{{ role.description }}</td>
                <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                  <button class="text-primary hover:text-primary/80 mr-3" @click="openEditRole(role)">编辑</button>
                  <button
                    :class="role.disabled ? 'text-success hover:text-success/80' : 'text-gray-500 hover:text-gray-700'"
                    @click="toggleRoleStatus(role)"
                  >
                    {{ role.disabled ? '启用' : '禁用' }}
                  </button>
                </td>
              </tr>
              </tbody>
            </table>
          </div>

          <!-- 角色添加/编辑弹窗 -->
          <div v-if="showRoleModal" class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40">
            <div class="bg-white p-6 rounded shadow-lg w-96">
              <h4 class="text-xl mb-4">{{ editingRole.id ? '编辑角色' : '添加角色' }}</h4>
              <form @submit.prevent="saveRole">
                <div class="mb-3">
                  <label class="block mb-1">角色名称</label>
                  <input v-model="editingRole.name" type="text" class="input w-full" required />
                </div>
                <div class="mb-3">
                  <label class="block mb-1">成员数</label>
                  <input v-model.number="editingRole.members" type="number" class="input w-full" min="1"  required/>
                </div>
                <div class="mb-3">
                  <label class="block mb-1">描述</label>
                  <textarea v-model="editingRole.description" class="input w-full" rows="3" required></textarea>
                </div>
                <div class="flex justify-end gap-3">
                  <button type="button" @click="closeRoleModal" class="btn btn-secondary">取消</button>
                  <button type="submit" class="btn btn-primary">{{ editingRole.id ? '保存' : '添加' }}</button>
                </div>
              </form>
            </div>
          </div>
        </div>









        <div v-if="activeTab === 'notifications'">
          <h3 class="font-bold text-gray-800 mb-6">通知设置</h3>
          <div class="space-y-6">
            <div><h4 class="font-medium text-gray-700">系统内通知</h4><p class="text-sm text-gray-500 mb-3">选择您想在系统仪表盘上看到的通知类型。</p><div class="space-y-2"><label class="flex items-center gap-2"><input type="checkbox" class="rounded" checked><span>出现一级预警时</span></label><label class="flex items-center gap-2"><input type="checkbox" class="rounded" checked><span>收到新任务指派时</span></label><label class="flex items-center gap-2"><input type="checkbox" class="rounded"><span>系统发布重要公告时</span></label></div></div>
            <div><h4 class="font-medium text-gray-700">邮件通知</h4><p class="text-sm text-gray-500 mb-3">选择您希望通过邮件接收的事件通知。</p><div class="space-y-2"><label class="flex items-center gap-2"><input type="checkbox" class="rounded" checked><span>每日安全报告</span></label><label class="flex items-center gap-2"><input type="checkbox" class="rounded"><span>每周数据摘要</span></label><label class="flex items-center gap-2"><input type="checkbox" class="rounded" checked><span>密码或安全设置被更改时</span></label></div></div>
          </div>
          <div class="flex justify-end gap-3 mt-6"><button class="btn btn-primary">保存设置</button></div>
        </div>



        <!--        <div v-if="activeTab === 'warnings'"><h3 class="font-bold text-gray-800 mb-6">预警规则管理</h3><div class="space-y-4"><div class="p-4 border rounded-lg"><h4 class="font-medium">一级预警 <span class="badge bg-danger text-white">即时推送</span></h4><p class="text-sm text-gray-500 my-2">当违法行为满足以下任一条件时，触发最高级别预警。</p><p><code>(违法类型 = 闯红灯 OR 逆行) AND (置信度 >= 0.8)</code></p></div><div class="p-4 border rounded-lg"><h4 class="font-medium">二级预警 <span class="badge bg-warning text-white">每日汇总</span></h4><p class="text-sm text-gray-500 my-2">当违法行为满足以下条件时，触发二级预警。</p><p><code>(违法类型 = 超速) AND (置信度 >= 0.6)</code></p></div></div><div class="text-right mt-6"><button class="btn btn-primary">编辑规则</button></div></div>-->




        <!-- 预警规则 -->
        <div v-if="activeTab === 'warnings'">
          <h3 class="font-bold text-gray-800 mb-6">预警规则管理</h3>
          <div class="space-y-4">
            <div v-for="(rule, index) in warningRules" :key="index" class="p-4 border rounded-lg">
              <h4 class="font-medium">
                {{ rule.level }}
                <span class="badge" :class="rule.badgeClass">{{ rule.triggerType }}</span>
              </h4>
              <p class="text-sm text-gray-500 my-2">
                {{ rule.description }}
              </p>
              <p><code>{{ rule.condition }}</code></p>

              <!-- 编辑按钮：绑定当前规则 -->
              <div class="mt-3 text-right">
                <button class="btn btn-sm btn-secondary" @click="openEditRuleModal(rule)">编辑规则</button>
              </div>
            </div>
          </div>

          <!-- 所有规则统一编辑入口（可选） -->
          <!-- <div class="text-right mt-6">
            <button class="btn btn-primary" @click="openEditAllRules">批量编辑</button>
          </div> -->

          <!-- 编辑规则弹窗 -->
          <div v-if="editWarningRuleModal" class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40 z-50">
            <div class="bg-white p-6 rounded shadow-lg w-96">
              <h4 class="text-xl mb-4">编辑预警规则</h4>
              <form @submit.prevent="saveWarningRule">
                <div class="mb-4">
                  <label class="block text-sm font-medium text-gray-700 mb-1">预警等级</label>
                  <input type="text" v-model="editingRule.level" class="input w-full" disabled />
                </div>
                <div class="mb-4">
                  <label class="block text-sm font-medium text-gray-700 mb-1">触发方式</label>
                  <input type="text" v-model="editingRule.triggerType" class="input w-full" disabled />
                </div>
                <div class="mb-4">
                  <label class="block text-sm font-medium text-gray-700 mb-1">描述信息</label>
                  <input type="text" v-model="editingRule.description" class="input w-full" />
                </div>
                <div class="mb-4">
                  <label class="block text-sm font-medium text-gray-700 mb-1">条件表达式</label>
                  <textarea v-model="editingRule.condition" rows="3" class="input w-full"></textarea>
                </div>
                <div class="flex justify-end gap-3 mt-6">
                  <button type="button" class="btn btn-secondary" @click="closeEditRuleModal">取消</button>
                  <button type="submit" class="btn btn-primary">保存规则</button>
                </div>
              </form>
            </div>
          </div>
        </div>












        <!-- 法规库管理 -->
        <div v-if="activeTab === 'rules'">
          <h3 class="font-bold text-gray-800 mb-6">法规库管理</h3>
          <p class="text-sm text-gray-500 mb-4">维护违法行为与交通法规的对应关系、处罚标准。</p>
<!--          &lt;!&ndash; 表格头部 + 添加按钮 &ndash;&gt;-->
<!--          <div class="flex justify-between items-center mb-4">-->
<!--            <h4 class="font-medium text-gray-700">法规列表</h4>-->
<!--            <button class="btn btn-primary" @click="openEditRule()">添加新条款</button>-->
<!--          </div>-->

          <!-- 表格头部 + 搜索 + 添加按钮 -->
          <div class="flex flex-col md:flex-row justify-between items-center mb-4 gap-2">
            <input
              v-model="searchRuleKeyword"
              type="text"
              placeholder="搜索违法类型或条款..."
              class="border px-3 py-1 rounded w-full md:w-1/3"
            />
            <button class="btn btn-primary" @click="openEditRule()">添加新条款</button>
          </div>


          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left">违法类型</th>
              <th class="px-6 py-3 text-left">对应条款</th>
              <th class="px-6 py-3 text-left">罚款(元)</th>
              <th class="px-6 py-3 text-left">记分</th>
              <th class="px-6 py-3 text-right">操作</th>
            </tr>
            </thead>
            <tbody class="divide-y">
           <tr v-for="rule in rules" :key="rule.ruleId">
  <td class="px-6 py-4">{{ rule.violationType }}</td>
  <td class="px-6 py-4">{{ rule.legalReference }}</td>
  <td class="px-6 py-4">{{ rule.baseFine }}</td>
  <td class="px-6 py-4">{{ rule.baseDemeritPoints }}</td>
  <td class="px-6 py-4 text-right">
    <button class="text-primary" @click="openEditRule(rule)">编辑</button>
    <button class="text-red-500" @click="deleteRule(rule.ruleId)">删除</button>
  </td>
</tr>

            </tbody>
          </table>


                   <!-- 分页区域 -->
<div class="mt-4 flex flex-col md:flex-row justify-between items-center text-sm text-gray-600">
  <div class="mb-2 md:mb-0">
    显示第 {{ (rulePagination.page - 1) * rulePagination.size + 1 }} 到
    {{ Math.min(rulePagination.page * rulePagination.size, rulePagination.total) }} 条，共 {{ rulePagination.total }} 条记录
  </div>
  <div class="flex items-center space-x-2">
    <button
      @click="handleRulePageChange(rulePagination.page - 1)"
      :disabled="rulePagination.page === 1"
      class="px-3 py-1 border rounded hover:bg-gray-100 disabled:opacity-50"
    >
      上一页
    </button>

    <span>第 {{ rulePagination.page }} / {{ Math.ceil(rulePagination.total / rulePagination.size) }} 页</span>

    <button
      @click="handleRulePageChange(rulePagination.page + 1)"
      :disabled="rulePagination.page >= Math.ceil(rulePagination.total / rulePagination.size)"
      class="px-3 py-1 border rounded hover:bg-gray-100 disabled:opacity-50"
    >
      下一页
    </button>
  </div>
</div>
        </div>


        <!-- 法规库编辑弹窗 -->
        <div v-if="showRuleModal" class="fixed inset-0 flex items-center justify-center bg-black bg-opacity-40 z-50">
          <div class="bg-white p-6 rounded shadow-lg w-96">
            <h4 class="text-xl mb-4">{{ editingLegalRule.id ? '编辑条款' : '添加新条款' }}</h4>
            <form @submit.prevent="saveRule">
              <div class="mb-3">
                <label class="block text-sm font-medium text-gray-700 mb-1">违法类型</label>
                <input v-model="editingLegalRule.violationType" type="text" class="input w-full" required />
              </div>
              <div class="mb-3">
                <label class="block text-sm font-medium text-gray-700 mb-1">对应条款</label>
                <input v-model="editingLegalRule.legalReference" type="text" class="input w-full" required />
              </div>
              <div class="mb-3">
                <label class="block text-sm font-medium text-gray-700 mb-1">罚款(元)</label>
                <input v-model.number="editingLegalRule.baseFine" type="number" class="input w-full" required min="0" />
              </div>
              <div class="mb-3">
                <label class="block text-sm font-medium text-gray-700 mb-1">记分</label>
                <input v-model.number="editingLegalRule.baseDemeritPoints" type="number" class="input w-full" required min="0" />
              </div>

              <div class="flex justify-end gap-3 mt-6">
                <button type="button" class="btn btn-secondary" @click="closeRuleModal">取消</button>
                <button type="submit" class="btn btn-primary">{{ editingLegalRule.id ? '保存' : '添加' }}</button>
              </div>
            </form>
          </div>
        </div>












        <!-- 系统参数 -->
        <div v-if="activeTab === 'params'">
          <h3 class="font-bold text-gray-800 mb-6">系统参数</h3>
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700">系统名称</label>
              <input type="text" value="城市交通智能执法平台" class="mt-1 block w-full input" />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">会话超时时间 (分钟)</label>
              <input type="number" value="30" class="mt-1 block w-full input" />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">数据保留策略 (天)</label>
              <input type="number" value="365" class="mt-1 block w-full input" />
              <p class="text-xs text-gray-500 mt-1">系统将自动清除超过此天数的历史操作日志和违法记录。</p>
            </div>
          </div>
          <div class="flex justify-end gap-3 mt-6">
            <button class="btn btn-primary">保存参数</button>
          </div>
        </div>

        <!-- 数据管理 -->
        <div v-if="activeTab === 'data'">
          <h3 class="font-bold text-gray-800 mb-6">数据管理</h3>
          <div class="space-y-6">
            <div class="p-4 border rounded-lg">
              <h4 class="font-medium">数据备份</h4>
              <p class="text-sm text-gray-500 my-2">创建系统数据的完整备份。上次备份时间：2025-06-08 02:00:00</p>
              <button class="btn btn-secondary">立即备份</button>
            </div>
            <div class="p-4 border border-warning/50 rounded-lg bg-warning/5">
              <h4 class="font-medium text-warning">数据恢复</h4>
              <p class="text-sm text-gray-500 my-2">从备份文件恢复数据。注意：此操作将覆盖现有数据且不可逆。</p>
              <input type="file" class="text-sm" />
              <button class="btn btn-warning ml-4">开始恢复</button>
            </div>
          </div>
        </div>





        <div v-if="activeTab === 'logs'">
          <h3 class="font-bold text-gray-800 mb-6">操作日志</h3>
          <div class="flex flex-col md:flex-row justify-between items-center mb-4 gap-3">
            <div class="relative w-full md:w-auto">
              <i class="fa fa-search absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></i>
              <input
                v-model="searchLogKeyword"
                type="text"
                placeholder="搜索操作或用户..."
                class="input pl-10 w-full"
              />
            </div>
            <input v-model="selectedDate" type="date" class="input w-full md:w-auto" />

          </div>

          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left">时间</th>
                <th class="px-6 py-3 text-left">用户</th>
                <th class="px-6 py-3 text-left">IP地址</th>
                <th class="px-6 py-3 text-left">操作详情</th>
              </tr>
              </thead>
             <tbody class="divide-y">
            <tr v-for="log in logs" :key="log.id">

  <td class="px-6 py-4">{{ log.time }}</td>
  <td class="px-6 py-4">{{ log.user }}</td>
  <td class="px-6 py-4">{{ log.ip }}</td>
  <td class="px-6 py-4">{{ log.action }}</td>

</tr>


             </tbody>

            </table>


            <div class="mt-4 flex flex-col md:flex-row justify-between items-center text-sm text-gray-600">
  <div class="mb-2 md:mb-0">
    显示第 {{ (logPagination.page - 1) * logPagination.size + 1 }} 到
    {{ Math.min(logPagination.page * logPagination.size, logPagination.total) }} 条，共 {{ logPagination.total }} 条记录
  </div>
  <div class="flex items-center space-x-2">
    <button
      @click="handleLogPageChange(logPagination.page - 1)"
      :disabled="logPagination.page === 1"
      class="px-3 py-1 border rounded hover:bg-gray-100 disabled:opacity-50"
    >
      上一页
    </button>

    <span>第 {{ logPagination.page }} / {{ Math.ceil(logPagination.total / logPagination.size) }} 页</span>

    <button
      @click="handleLogPageChange(logPagination.page + 1)"
      :disabled="logPagination.page >= Math.ceil(logPagination.total / logPagination.size)"
      class="px-3 py-1 border rounded hover:bg-gray-100 disabled:opacity-50"
    >
      下一页
    </button>
  </div>
</div>



          </div>
        </div>











      </div>
    </div>
  </div>






</template>





<script setup>
// import { ref, onMounted } from 'vue'
import {ref, onMounted, computed, watch} from 'vue'

import axios from 'axios'

const activeTab = ref('notifications')
const searchKeyword = ref('')

// import request from '@/utils/request' // 注意路径
// 错误修正：从直接使用 axios 改为使用我们配置好的 apiClient
import apiClient from '@/services/api';

// 控制弹窗显示
const showUserModal = ref(false)
// 当前编辑或添加的用户对象
const editingUser = ref({})

// const user = ref({ name: '', email: '', phone: '' })

const user = ref({})
const users = ref([])


const avatarFileInput = ref(null)

// 触发文件选择
function triggerUpload() {
  avatarFileInput.value.click()
}

// 处理上传的头像文件
function handleAvatarChange(event) {
  const file = event.target.files[0]
  if (file && file.type.startsWith('image/')) {
    const reader = new FileReader()
    reader.onload = () => {
      user.value.avatar = reader.result // 更新预览
    }
    reader.readAsDataURL(file)
  } else {
    alert('请选择有效的图片文件')
  }
}


const userAvatarInput = ref(null)

function triggerUserAvatarUpload() {
  userAvatarInput.value.click()
}

function handleUserAvatarChange(event) {
  const file = event.target.files[0]
  if (file && file.type.startsWith('image/')) {
    const reader = new FileReader()
    reader.onload = () => {
      editingUser.value.avatar = reader.result // 更新为 base64 数据
    }
    reader.readAsDataURL(file)
  } else {
    alert('请选择有效的图片文件')
  }
}


const total = ref(0)         // 总条数
const page = ref(1)          // 当前页
const size = ref(5)          // 每页数量


// 单独写请求函数
async function fetchUserData() {
  try {
    const response = await apiClient.get('http://localhost:8080/api/users/getUser')
    user.value = response.data
  } catch (error) {
    console.error('请求失败：', error)
  }
}


async function fetchUsersData() {
  try {
    // console.log('fetchUsers 被调用了，当前 page:', page.value)
    const response = await apiClient.get('/users/getUsers', {
      params: {
        page: page.value,
        size: size.value,
        keyword: searchKeyword.value
      }
    })
    users.value = response.data.data
    total.value = response.data.total
  } catch (error) {
    console.error('请求失败：', error)
  }
}



// 打开添加用户弹窗
function openAddUser() {
  editingUser.value = {
    id: null,
    name: '',
    email: '',
    role: '',
    status: 'active',
    avatar: '',
    lastLogin: '',
  }
  showUserModal.value = true
}

// 打开编辑用户弹窗，传入用户数据
function openEditUser(user) {
  editingUser.value = {...user}  // 克隆一份，避免直接修改原数组
  showUserModal.value = true
}

// 关闭弹窗
function closeUserModal() {
  showUserModal.value = false
}

// 保存用户（添加或编辑）
async function saveUser() {
  try {
    if (editingUser.value.id) {
      // 编辑用户：找到索引，更新数据

      const index = users.value.findIndex(u => u.id === editingUser.value.id)
      if (index !== -1) {
        users.value[index] = {...editingUser.value}
      }
      //添加新用户调用后端
      const response = await axios.post('http://localhost:8080/api/users', editingUser.value)


    } else {
      // 添加用户：生成新id（简单处理）
      editingUser.value.id = Date.now()
      users.value.push({...editingUser.value})
      //更新用户后端
      await axios.put(`http://localhost:8080/api/users/${editingUser.value.id}`, editingUser.value)
    }
  } catch (error) {
    console.error('保存用户失败：', error)
  }
  closeUserModal()
}

// 切换用户状态
function toggleUserStatus(user) {
  const index = users.value.findIndex(u => u.id === user.id)
  if (index !== -1) {
    users.value[index].status = users.value[index].status === 'active' ? 'disabled' : 'active'
  }
}


// 搜索关键词变化时重新加载第一页
watch(searchKeyword, () => {
  page.value = 1
  fetchUsersData()
})

const handlePageChange = (newPage) => {
  // console.log('跳转页码:', newPage) // 确认是否触发
  if (newPage < 1 || newPage > Math.ceil(total.value / size.value)) return
  page.value = newPage
  fetchUsersData()
}

const filteredUsers = computed(() => users.value) // 后端分页，不需要本地过滤











const logPagination = ref({
  page: 1,
  size: 5,
  total: 0
})

// 操作日志数据源
const logs = ref([
])

// 搜索关键词
const searchLogKeyword = ref('')
const selectedDate = ref('')

async function fetchLogsData() {
  try {
    const { page, size } = logPagination.value
    const keyword = searchLogKeyword.value || ''
    const date = selectedDate.value || ''

    const response = await apiClient.get('/logs/page', {
      params: {
        page,
        size,
        keyword,
        date
      }
    })

    logs.value = response.data.items.map(log => ({
      id: log.logId,
      user: log.username, // ✅ 修正
      actionType: log.actionType,
      targetType: log.targetEntityType,
      targetId: log.targetEntityId,
      action: log.details,
      ip: log.clientIpAddress,
      time: new Date(log.createdAt).toLocaleString('zh-CN', { hour12: false })
    }))

    logPagination.value.total = response.data.totalItems // ✅ 修正字段名

    // console.log('fetchLogsData:', response.data)
    console.log("logs", logs.value)
  } catch (error) {
    console.error('获取日志失败:', error)
    alert('获取日志失败')
  }
}


watch([searchLogKeyword, selectedDate], () => {
  logPagination.value.page = 1
  fetchLogsData()
})


function handleLogPageChange(newPage) {
  const maxPage = Math.ceil(logPagination.value.total / logPagination.value.size)
  if (newPage < 1 || newPage > maxPage) return
  logPagination.value.page = newPage
  fetchLogsData()
}















// === 法规库模块相关 ===




// 法规模块分页
const rulePagination = ref({
  page: 1,
  size: 5,
  total: 0
})

const rules = ref([
])
const searchRuleKeyword = ref('')

async function fetchrulesData() {
  try {
    const { page, size } = rulePagination.value
    const offset = (page - 1) * size
    const response = await apiClient.get('/rules/page', {
      params: {
        page,
        size,
        searchRuleKeyword: searchRuleKeyword.value // ✅ 正确：拿到实际字符串值
      }
    })

    // console.log('fetchrulesData:', response.data)

    rules.value = response.data.items.map(rule => ({
      ruleId: rule.ruleId,
      violationType: rule.violationType,
      legalReference: rule.legalReference,
      baseFine: rule.baseFine,
      baseDemeritPoints: rule.baseDemeritPoints
    }))

    rulePagination.value.total = response.data.total
  } catch (error) {
    console.error('请求失败：', error)
  }
}



const showRuleModal = ref(false)
// const editingLegalRule = ref(null)
const editingLegalRule = ref({
  ruleId: null,
  violationType: '',
  legalReference: '',
  baseFine: 0,
  baseDemeritPoints: 0
})


function openEditRule(rule = null) {
  // console.log('openEditRule', rule)
  // console.log(showRuleModal.value)
  editingLegalRule.value = rule ? {
    ruleId: rule.ruleId,
    violationType: rule.violationType,
    legalReference: rule.legalReference,
    baseFine: rule.baseFine,
    baseDemeritPoints: rule.baseDemeritPoints
  } : {
    ruleId: null,
    violationType: '',
    legalReference: '',
    baseFine: null,
    baseDemeritPoints: null
  }
  showRuleModal.value = true
}


async function saveRule() {
  const { ruleId, violationType, legalReference, baseFine, baseDemeritPoints } = editingLegalRule.value

  if (!violationType || !legalReference || baseFine === null || baseDemeritPoints === null) {
    alert('请完整填写所有字段')
    return
  }

  const payload = {
    ruleId,
    violationType,
    legalReference,
    baseFine: parseFloat(baseFine),
    baseDemeritPoints: parseInt(baseDemeritPoints)
  }

  try {
    if (ruleId) {
      // 编辑已有规则
      await apiClient.put(`/rules/${ruleId}`, payload)
    } else {
      // 新增规则
      const res = await apiClient.post('/rules', payload)
      payload.ruleId = res.data.ruleId
    }


    // 更新本地列表
    const index = rules.value.findIndex(r => r.ruleId === ruleId)
    await fetchrulesData();
  } catch (error) {
    console.error('保存规则失败:', error)
    alert('保存失败，请重试')
  }

  closeRuleModal()
}


function closeRuleModal() {
  showRuleModal.value = false
  editingLegalRule.value = null
}

function handleRulePageChange(newPage) {
  const maxPage = Math.ceil(rulePagination.value.total / rulePagination.value.size)
  if (newPage < 1 || newPage > maxPage) return
  rulePagination.value.page = newPage
  fetchrulesData()
}



// 删除操作
const  deleteRule = async (ruleId) => {
  // if (confirm('确定要删除该条法规吗？')) {
  //   rules.value = rules.value.filter(rule => rule.ruleId !== ruleId)
  //   if (rulePagination.value.page > Math.ceil(filteredRules.value.length / rulePagination.value.size)) {
  //     rulePagination.value.page = Math.max(1, rulePagination.value.page - 1)
  //   }
  // }



  // console.log('deleteRule', ruleId)
  if (!confirm('确定要删除该条法规吗？')) return

  try {
    // 调用后端删除接口
    await apiClient.delete(`/rules/${ruleId}`)

    // 从本地列表中移除该条规则
    rules.value = rules.value.filter(rule => rule.ruleId !== ruleId)

    // 如果当前页数据为空，则跳转到上一页
    const totalItems = rulePagination.value.total - 1
    const maxPage = Math.ceil(totalItems / rulePagination.value.size)
    if (rulePagination.value.page > maxPage && maxPage >= 1) {
      rulePagination.value.page = maxPage
    }

    // 更新分页总数
    rulePagination.value.total = totalItems

    // 可选：刷新当前页数据以保持一致性（或根据返回结果重新加载）
    fetchrulesData()
  } catch (error) {
    console.error('删除法规失败:', error)
    alert('删除失败，请重试')
  }






}

// 搜索时自动重置页码为第一页
watch(searchRuleKeyword, () => {
  fetchrulesData()
  rulePagination.value.page = 1
})





























// 角色权限管理模块相关

// 控制弹窗显示
const showRoleModal = ref(false)
// 当前编辑或添加的角色
const editingRole = ref({})


// 当前角色列表
const roles = ref([
  {
    id: 1,
    name: '系统管理员',
    members: 5,
    description: '拥有系统所有权限，可管理所有功能',
    disabled: false
  },
  {
    id: 2,
    name: '交通执法员',
    members: 42,
    description: '可查看和处理违法记录，管理设备',
    disabled: false
  },
  {
    id: 3,
    name: '数据分析师',
    members: 8,
    description: '可查看统计报表，无管理权限',
    disabled: false
  },
  {
    id: 4,
    name: '设备维护员',
    members: 12,
    description: '可管理监控设备，查看设备状态',
    disabled: false
  },
])


// 打开添加角色弹窗
function openAddRole() {
  editingRole.value = {
    id: null,
    name: '',
    members: null,
    description: '',
    disabled: false,
  }
  showRoleModal.value = true
}

// 打开编辑角色弹窗
function openEditRole(role) {
  editingRole.value = {...role} // 克隆避免直接修改
  showRoleModal.value = true
}

// 关闭弹窗
function closeRoleModal() {
  showRoleModal.value = false
}

// 保存角色（新增或编辑）
function saveRole() {
  if (editingRole.value.id) {
    // 编辑角色
    const index = roles.value.findIndex(r => r.id === editingRole.value.id)
    if (index !== -1) {
      roles.value[index] = {...editingRole.value}
    }
  } else {
    // 添加角色，简单生成id
    editingRole.value.id = Date.now()
    roles.value.push({...editingRole.value})
  }
  closeRoleModal()
}

// 切换角色状态（禁用/启用）
function toggleRoleStatus(role) {
  const index = roles.value.findIndex(r => r.id === role.id)
  if (index !== -1) {
    roles.value[index].disabled = !roles.value[index].disabled
  }
}







// 预警规则数据
const warningRules = ref([
  {
    level: '一级预警',
    badgeClass: 'bg-danger text-white',
    triggerType: '即时推送',
    description: '当违法行为满足以下任一条件时，触发最高级别预警。',
    condition: '(违法类型 = 闯红灯 OR 逆行) AND (置信度 >= 0.8)',
  },
  {
    level: '二级预警',
    badgeClass: 'bg-warning text-white',
    triggerType: '每日汇总',
    description: '当违法行为满足以下条件时，触发二级预警。',
    condition: '(违法类型 = 超速) AND (置信度 >= 0.6)',
  }
])

// 控制预警规则弹窗显示
const editWarningRuleModal = ref(false)
// 当前正在编辑的规则
const editingRule = ref(null)

// 打开编辑规则弹窗
function openEditRuleModal(rule) {
  editingRule.value = {...rule} // 克隆当前规则用于编辑
  editWarningRuleModal.value = true
}

// 保存编辑后的规则
function saveWarningRule() {
  if (!editingRule.value) return

  const index = warningRules.value.findIndex(r => r.level === editingRule.value.level)
  if (index !== -1) {
    warningRules.value[index] = {...editingRule.value}
  }

  closeEditRuleModal()
}

// 关闭弹窗
function closeEditRuleModal() {
  editWarningRuleModal.value = false
}


watch(activeTab, (newVal) => {
  if (newVal === 'users') {
    fetchUsersData()
  } else if (newVal === 'logs') {
    fetchLogsData()
  }
})


// 挂载时调用请求函数
onMounted(() => {
  fetchUserData()
  fetchrulesData()
})


</script>
