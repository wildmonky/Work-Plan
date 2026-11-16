/**
 * Created by Administrator on 2016/4/24.
 */
var _sys_title = '';
var _sys_serverpath = "/kemp/ex_web/oms/omsserver";
var _sys_baseurl = window.location.protocol + "//" + window.location.host + _sys_serverpath;
var _sys_rootpath = "/kemp/ex_web/oms/app";

function updateUrlWithFormParams(form) {
	// 获取当前URL的路径和哈希部分（不包括查询参数）
	const url = new URL(window.location.href);
	const pathAndHash = url.origin + url.pathname + url.hash;

	// 解析当前URL参数，过滤空值
	const currentParams = new URLSearchParams(url.search);
	const newParams = new URLSearchParams();

	// 添加非空参数到新参数集合
	for (const [key, value] of currentParams.entries()) {
		if (value.trim() !== '') {
			newParams.append(key, value);
		}
	}

	// 处理表单元素
	const elements = form.elements;
	for (let i = 0; i < elements.length; i++) {
		const element = elements[i];
		const name = element.name;
		const tagName = element.tagName.toLowerCase();

		// 跳过无名称元素
		if (!name) continue;

		// 处理select元素
		if (tagName === 'select') {
			const options = element.options;
			const selectedOptions = Array.from(options).filter(opt => opt.selected);

			// 删除旧值（如果存在）
			if (selectedOptions.length > 0) {
				newParams.delete(name);
			}

			// 添加新选择的值
			for (const option of selectedOptions) {
				const value = option.value;
				if (value.trim() !== '') {
					newParams.append(name, value);
				}
			}
		}
		// 处理input元素
		else if (tagName === 'input') {
			const type = element.type.toLowerCase();
			let value = element.value;

			// 跳过未选中的单选/复选框
			if ((type === 'radio' || type === 'checkbox') && !element.checked) {
				continue;
			}

			// 跳过空值
			if (value.trim() === '') continue;

			// 替换/添加值
			newParams.delete(name);  // 删除所有旧值
			newParams.append(name, value);
		}
	}

	// 构建新URL
	const queryString = newParams.toString();
	return queryString ? `${pathAndHash.split('?')[0]}?${queryString}` : pathAndHash.split('?')[0];
}

function getUrlKey(name) {
	return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').
		exec(location.href) || [, ""])[1].replace(/\+/g, '%20')) || null;
}

function showlayer(url, area1, area2, reload_flag) {
	var index = layer.open({
		type: 2,
		content: url,
		offset: ['10px', '10px'],
		area: ["99%", "99%"],
		//area:['100px','100px'],
		maxmin: true,
		move: false,
		end: function () {
			if (reload_flag == 1) {
				location.reload();
			}
		}
	});
}

Vue.filter("addnum", function (value, num) {
	return parseFloat(num) + parseFloat(value);
});

//40：选择开关
Vue.component('dataadd-input-switch', {
	props: ['groupextra', 'ishide', 'columncode', 'columnval', 'nullable', 'columnlength'],

	template: `<div class="switch":id="columncode">
                    <input class="testswitch-checkbox" 
					:name="columncode" type="checkbox">
                </div>`
	// 	template: `<div class="testswitch">
	// 					<input class="testswitch-checkbox" 
	// 					:id="columncode" :name="columncode" type="checkbox" 
	// 					:value="columnval"
	// 					:required="((nullable=='0') || (nullable=='N'))?true:false" >
	// 					<label class="testswitch-label" forbak="onoffswitch">
	// 						<span class="testswitch-inner" 
	// 							data-on="ON" data-off="OFF" :for="columncode"></span>
	// 						<span class="testswitch-switch"></span>
	// 					</label>
	// 				</div>`
});

Vue.component('dataadd-input-selinput', {
	props: ['datamap', 'columncode', 'columnname', 'columnval'],

	template: `<div class="col-xl-6 col-md-12 col-sm-12 ">
				<div class="mb-3 form-group d-flex align-items-start">
					<input type="hidden" :name="columncode" 
						:id="columncode" :value="columnval"/>
					<div class="dropdown" @click.stop>
						<!-- 下拉按钮 -->
						<a class="btn btn-primary dropdown-toggle w-100"
											role="button"
											data-toggle="dropdown"
											aria-expanded="false">
							<span v-if="columnval">
								{{columnval}}
							</span>
							<span v-else>请选择</span>
						</a>
						
						<!-- 下拉菜单 -->
						<div class="dropdown-menu dropdown-menu-left p-3" 
						style="width: 100%;">
							<!-- 单选列表 -->
							<div v-for="(text,value) in datamap" 
							:key="value" class="form-check">
								<input type="radio"
									class="form-check-input"
									:id="'item-' + value"
									:value="value"
									
								/>
								<label class="form-check-label" 
								:for="'item-' +value">
									{{text}}
								</label>
							</div>
							<!-- 新增内容 -->
							<div class="mb-2">
								<input 
									class="form-control"
										type="text" 
										placeholder="添加新内容"
										onBlur="handleBlur(this)"
								/>
								</div>
							</div>
						</div>
					</div>
				</div>	`
});

Vue.component('select-ok-nook', {
	props: ['deviceid', 'moreflag', 'timeflag'],
	template: `<div><div class="dot-all">
		<div class="dropdown dropdown-dot list">
			<input v-if="moreflag=='0'" type="hidden" :name="'device_'+timeflag+'_'+deviceid" :id="'device_'+timeflag+'_'+deviceid"
				:deviceid="deviceid" value="">
			<a class="arrow-none dropdown-toggle" role="button" data-toggle="dropdown" aria-expanded="false">
				<i class="bi bi-three-dots-vertical"></i>
			</a>
			<div class="dropdown-menu dropdown-menu-right dropdown-menu-lg-left" style="min-width: auto;">
				<a class="dropdown-item tisok"><i class="bi bi-check-lg ok" 
				:deviceid="deviceid" :timeflag="timeflag" :moreflag="moreflag" onclick="selectok(this,1)"></i></a>
				<a class="dropdown-item tisno"><i class="bi bi-x-lg no" 
				 :deviceid="deviceid" :timeflag="timeflag" :moreflag="moreflag" onclick="selectok(this,0)"></i></a>
			</div>
		</div>
		</div>
		<div class="tis">
		<span :id="'tis_ok_'+timeflag+'_'+deviceid" class="tis-item"><i class="bi bi-check-lg ok"></i></span>
		<span :id="'tis_no_'+timeflag+'_'+deviceid" class="tis-item"><i class="bi bi-x-lg no"></i></span>
		</div></div>`
});

//1：单行文本输入框
Vue.component('dataadd-input-text', {
	props: ['groupextra', 'ishide', 'columncode', 'columnval', 'nullable', 'columnlength'],
	template: "<input :disabled=\"groupextra!='1'?'disabled':false\" :readonly=\"ishide==1?'readonly':false\" type='text' class='form-control' " +
		":required=\"((nullable=='0') || (nullable=='N'))?true:false\" " +
		":name=\"columncode\" :id='columncode' :maxlength='columnlength' :value='columnval' />"
});

//2：多行文本输入框
Vue.component('dataadd-input-textarea', {
	props: ['groupextra', 'ishide', 'columncode', 'columnval', 'nullable', 'columnlength'],
	template: "<textarea :disabled=\"groupextra!='1'?'disabled':false\" rows=5 :readonly=\"ishide==1?'readonly':false\" type='text' class='form-control' " +
		":required=\"((nullable=='0') || (nullable=='N'))?true:false\" " +
		":name='columncode' :id='columncode' :maxlength='columnlength'>{{columnval}}</textarea>"
});

//3：日期选择输框
Vue.component('dataadd-input-date', {
	props: ['groupextra', 'ishide', 'columncode', 'columnval', 'nullable', 'columnlength', 'inputType', 'dateType'],
	template: "<input :disabled=\"groupextra!='1'?'disabled':false\" :readonly=\"ishide==1?'readonly':false\" type='text' _other='time' class='datetimepicker form-control' " +
		":required=\"((nullable=='0') || (nullable=='N'))?true:false\" " +
		":name=\"columncode\" :id='columncode' :maxlength='columnlength' :value='columnval' />",
	beforeCreate() {
		console.log('comA...beforeCreate...')
	},
	created() {
		console.log('comA...created...')
	},
	beforeMount() {
		console.log('comA...beforeMount...')
	},
	mounted() {
		console.log('comA...mounted...');
		console.log(this.columnval, this.inputType, this.dateType)


		let time = new Date()
		laydate.render({
			elem: '#' + this.columncode, //指定元素
			type: 'datetime',
			format: this.inputType == 3 ? 'yyyy-MM-dd HH:mm:ss' : 'yyyy-MM-dd' || 'yyyy-MM-dd',
			isInitValue: true,
			// value: formattedTime
		});
	}
});



//4：日期选择输框
Vue.component('dataadd-input-datenotime', {
	props: ['groupextra', 'ishide', 'columncode', 'columnval', 'nullable', 'columnlength'],
	template: "<input :disabled=\"groupextra!='1'?'disabled':false\" :readonly=\"ishide==1?'readonly':false\" type='date' _other='time' class='datetimepicker form-control' " +
		":required=\"((nullable=='0') || (nullable=='N'))?true:false\" " +
		":name=\"columncode\" :id='columncode' :maxlength='columnlength' :value='columnval' />"
});

//5：key-value数组（单select)
Vue.component('dataadd-input-kvdata', {
	props: ['groupextra', 'ishide', 'columncode', 'columnval', 'nullable', 'columnlength', 'datamap'],
	template: "<select  class=\"form-control\" :disabled=\"groupextra!='1'?'disabled':false\" :readonly=\"ishide==1?'readonly':false\" " +
		":required=\"((nullable=='0') || (nullable=='N'))?true:false\" " +
		":name=\"columncode\" :id='columncode' :value='columnval' >" +
		"<option v-for=\"(text,value) in datamap\"  :value='value'>{{text}}</option></select>",
});

//21：key-value数组（单select，可修改)
Vue.component('dataadd-input-editselect', {
	props: ['groupextra', 'ishide', 'columncode', 'columnval', 'nullable', 'columnlength', 'datamap'],
	template: "<select :disabled=\"groupextra!='1'?'disabled':false\" " +
		":readonly=\"ishide==1?'readonly':false\" class='edit-select form-control' " +
		":required=\"((nullable=='0') || (nullable=='N'))?true:false\" " +
		":name=\"columncode\" :id='columncode' >" +
		"<option v-for=\"(text,value) in datamap\" :value='value' :selected='columnval==value'>{{text}}</option></select>"
});

//41：三级联动选择器（elementUI组件库)
Vue.component('dataadd-input-cascader', {
    props: ['groupextra','ishide','columncode','columnval','nullable', 'columnlength','datamap','collapseTags', 'hiddenId'],
    data: function () {
        return {
            cascaderProps: {
                multiple: true,	//多选
                emitPath: true,
                checkStrictly: false,
                value: 'value',
                label: 'label',
                children: 'children'
            }
        }
    },
    computed: {
		cascaderOptions: function () {
			if (Array.isArray(this.datamap)) {
				return this.datamap;
			}
			if (this.datamap && Array.isArray(this.datamap.optionsList)) {
				return this.datamap.optionsList;
			}
			return [];
		},

        currentValue: {
            get: function () {
				if (Array.isArray(this.columnval)) {
					return this.columnval;
				}
				if (typeof this.columnval === 'string') {
					try {
						var val = JSON.parse(this.columnval);

						if (Array.isArray(val)) {
							return val;
						}
					} catch (e) {
						console.log('columnval JSON解析失败:', this.columnval);
					}
				}
				return [];
            },
            set: function (val) {
				val = Array.isArray(val) ? val : [];

				this.$emit('input', val);
				this.$emit('change', val);
            }
        },
		hiddenValue: function () {
			return JSON.stringify(this.currentValue || []);
		}
    },
	methods:{
		handleClear:function(){
			this.$emit('input', []);
			this.$emit('change', []);
		}
	},
    template: `
		<div class="w-100">
			<el-cascader
				class="w-100"
				:name="columncode"
				:disabled="groupextra!='1'"
				v-model="currentValue"
				:options="cascaderOptions"
				:props="cascaderProps"
				:show-all-levels="false"
				:collapse-tags="collapseTags"
				clearable
				@clear="handleClear">
			</el-cascader>
			<input
				type="hidden"
				:id="hiddenId"
				:name="columncode"
				:value="hiddenValue">
		</div>		
    `
});

//6：SQL数据选择输入框（单select)
Vue.component('dataadd-input-sqldata', {
	props: ['groupextra', 'ishide', 'columncode', 'columnval', 'nullable', 'columnlength', 'datamap'],
	template: "<select :disabled=\"groupextra!='1'?'disabled':false\" :readonly=\"ishide==1?'readonly':false\" class=\"form-control\" " +
		":required=\"((nullable=='0') || (nullable=='N'))?true:false\" " +
		":name=\"columncode\" :id='columncode' :value='columnval' >" +
		"<option v-for=\"(dataitem,index) in datamap\"  :value='dataitem.FLDN_K'>{{dataitem.FLDN_V}}</option></select>"
});

//9：数字输入框
Vue.component('dataadd-input-number', {
	props: ['groupextra', 'ishide', 'columncode', 'columnval', 'nullable', 'columnlength'],
	template: "<input step='1' :disabled=\"groupextra!='1'?'disabled':false\" :readonly=\"ishide==1?'readonly':false\" type='number' class='form-control' " +
		":required=\"((nullable=='0') || (nullable=='N'))?true:false\" " +
		":name=\"columncode\" :id='columncode' :maxlength='columnlength' :value='columnval' />"
});

//10：文件选择框
Vue.component('dataadd-input-file', {
	props: ['atta_src_id', 'tablename', 'groupextra', 'ishide', 'columncode', 'columnval', 'nullable', 'columnlength'],
	template: "<div ><input :disabled=\"groupextra!='1'?'disabled':false\" :readonly=\"ishide==1?'readonly':false\" type='file' class='form-control' " +
		":required=\"((nullable=='0') || (nullable=='N'))?true:false\" " +
		":name=\"columncode\" :id='columncode' :maxlength='columnlength'/>" +
		"<div  class='px-3 pt-3' v-for=\"(valitem,valindex) in columnval\">" +
		"<a target=_blank :href='\"/kemp/ex_web/oms/omsserver/Flow/AttaDownload?atta_id=\"+valitem.id'>{{valitem.name}}</a>" +
		"<button :atta_src_id='atta_src_id' :tablename='tablename' :fieldname='columncode' :atta_id='valitem.id' onclick='deleteAttaFile(this)' type='button' class='close text-white font-weight-normal'>" +
		"<span aria-hidden='true'>&times;</span></button></div></div>"
});

//12：SQL数据选择输入框（多select)
Vue.component('dataadd-input-sqldatamult', {
	props: ['groupextra', 'ishide', 'columncode', 'columnval', 'nullable', 'columnlength', 'datamap'],
	template: "<select multiple :readonly=\"ishide==1?'readonly':false\" class='form-control selectpicker' " +
		":required=\"((nullable=='0') || (nullable=='N'))?true:false\" " +
		":name=\"columncode\" :id='columncode' :sel_values='columnval' >" +
		"<option v-for=\"(dataitem,index) in datamap\"  :value='dataitem.FLDN_K'>{{dataitem.FLDN_V}}</option></select>"
});

//25：数据选择输入框（多select)
Vue.component('dataadd-input-kvmult', {
	props: ['groupextra', 'ishide', 'columncode', 'columnval', 'nullable', 'columnlength', 'datamap'],
	template: "<select multiple :readonly=\"ishide==1?'readonly':false\" class='form-control selectpicker' " +
		":required=\"((nullable=='0') || (nullable=='N'))?true:false\" " +
		":name=\"columncode\" :id='columncode' :sel_values='columnval' >" +
		"<option v-for=\"(text,value) in datamap\"  :value='value'>{{text}}</option></select>"
});

//13：密码输入框
Vue.component('dataadd-input-password', {
	props: ['groupextra', 'ishide', 'columncode', 'columnval', 'nullable', 'columnlength'],
	template: "<input :disabled=\"groupextra!='1'?'disabled':false\" :readonly=\"ishide==1?'readonly':false\" type='password' class='form-control' " +
		":required=\"((nullable=='0') || (nullable=='N'))?true:false\" " +
		":name=\"columncode\" :id='columncode' :maxlength='columnlength' :value='columnval' />"
});

//13：密码输入框:修改
Vue.component('dataadd-input-password-edit', {
	props: ['groupextra', 'ishide', 'columncode', 'nullable', 'columnlength'],
	template: "<input :disabled=\"groupextra!='1'?'disabled':false\" :readonly=\"ishide==1?'readonly':false\" type='password' class='form-control' " +
		":name=\"columncode\" :id='columncode' :maxlength='columnlength'  />"
});

//10：文件选择框(查看版本)
Vue.component('dataadd-input-file-view', {
	props: ['columncode', 'columnval'],
	template: "<div><span v-for=\"(valitem,valindex) in columnval\">"
		+ "<a style=\"color: rgb(243,217,157);text-decoration: underline;\" target=_blank :href='\"/kemp/ex_web/oms/omsserver/Flow/AttaDownload?atta_id=\"+valitem.id'>{{valitem.name}}</a>"
		+ "</span></div>"
});

Vue.component('server-key', {
	props: ['server_key', 'server_iv'],
	template: "<div><input type='hidden' id='_server_key_' name='_server_key_' v-bind:value='server_key' />" +
		"<input type='hidden' id='_server_iv_' name='_server_iv_' v-bind:value='server_iv' />" +
		"<input type='hidden' id='_client_sign_' name='_client_sign_' value='' /></div>"
});

var vueApp = null;

//obj_id：对象id
//data_info：返回的数据
//noChangeTitle：标志位，是否修改页面标题,1表示不修改，其余表示修改
function vueDisplay(obj_id, data_info, noChangeTitle, vueObj, func) {
	//console.log(data_info);
	if (data_info.result == "1") {
		var data_content = data_info.data;

		if (vueObj == undefined) {
			vueObj = vueApp;
		}

		if (vueObj == null) {
			vueObj = new Vue({
				filters: {
					nl2br(value) {
						return value.replace("\n", "<br>");
					},
					// 自定义过滤器函数
					formatNumber(value) {
						if (isNaN(value)) {
							return value;
						}

						const formatter = new Intl.NumberFormat(undefined,
							{ maximumFractionDigits: 0 }
						);
						return formatter.format(value).replace(",", "");
					},

					formatNumber2(value) {
						if (isNaN(value)) {
							return "";
						}

						const formatter = new Intl.NumberFormat(undefined,
							{ maximumFractionDigits: 2 }
						);
						return formatter.format(value).replace(",", "");
					}
				},

				el: obj_id,
				data: data_content,
				mounted() {
					if (func) {
						func(this)
					}
				}
			});

		} else {
			Vue.set(vueObj, "data", data_content);
		}

		if (noChangeTitle != 1) {
			if (data_content.page_title != undefined) {
				document.title = data_content.page_title + '-' + data_content.sys_title;
			} else if (data_content.datainfo != undefined && data_content.datainfo.title != undefined) {
				document.title = data_content.datainfo.title + '-' + data_content.sys_title;
			} else {
				document.title = data_content.sys_title;
			}
		}

	} else {
		var err_code = data_info.err_code;
		if ((err_code == "300099") || (err_code == "300098")) {
			//重新登录;
			var urlhost = window.location.host;
			if ((urlhost.includes("10.150") || urlhost.includes("192.3.1"))) {
				//东方IP，提示边缘
				layer.alert("您的登录信息或已失效，因为安全策略，请从边缘平台重新登录，或是重新在边缘平台上打开本模块。谢谢！");
			} else {
				var url_login = "login.html?err_code=" + err_code;
				if (window.parent.parent) {
					window.parent.parent.location = url_login;
				} else if (window.parent) {
					window.parent.location = url_login;
				} else {
					window.location = url_login;
				}
			}
		} else {
			//alert(data_info.err_msg);
		}
	}

}

/* 
* 替换URL的参数值
* url 目标url
* arg 需要替换的参数名称 
* arg_val 替换后的参数的值 
* return url 参数替换后的url 
*/
function changeURLArg(url, arg, arg_val) {
	var pattern = arg + '=([^&]*)';
	var replaceText = arg + '=' + arg_val;
	if (url.match(pattern)) {
		var retuenUrl = url;
		var temp = '/(\&' + arg + '=)([^&]*)/gi';
		if (eval(temp).test(retuenUrl)) {
			retuenUrl = retuenUrl.replace(eval(temp), '&' + replaceText);
		}
		var temps = '/([\?])(' + arg + '=)([^&]*)/gi';
		if (eval(temps).test(retuenUrl)) {
			retuenUrl = retuenUrl.replace(eval(temps), '?' + replaceText);
		}
		return retuenUrl;
	}
	else {
		// if (url.match('[\?]')) {
		if (url.indexOf('?') > 0) {
			return url + "&" + replaceText;
		} else {
			return url + "?" + replaceText;
		}
	}
	return url + '\n' + arg + '\n' + arg_val;
}

function beforeSubmit(obj) {
	var f = obj.form;

	var bv = $(f).data('bootstrapValidator');
	var flag = false;

	if (bv == undefined) {
		flag = true;
	} else {
		bv.validate();
		flag = bv.isValid();
	}
	if (flag) {
		var obj1 = $(f).serialize();
		var encrypted = Encrypt(obj1);

		$(f).find('input,textarea').each(function () {
			var n = $(this).attr("name");
			var t = $(this).attr("type");
			if ((n != '_server_key_') && (n != '_server_iv_')) {
				if ((t != 'submit') && (t != 'file')) {
					$(this).val('');
				}
			}
		});

		$("#_client_sign_").val(encrypted);

		f.submit(true);
	}
}
//加密
function Encrypt(word) {
	var key = CryptoJS.enc.Utf8.parse($("#_server_key_").val());
	var iv = CryptoJS.enc.Utf8.parse($("#_server_iv_").val());

	srcs = CryptoJS.enc.Utf8.parse(word);
	var encrypted = CryptoJS.AES.encrypt(srcs, key, {
		iv: iv,
		mode: CryptoJS.mode.CBC,
		padding: CryptoJS.pad.Pkcs7
	});
	return encrypted.toString();
}

//加密
function Encrypt_key(word, key, iv) {
	key = CryptoJS.enc.Utf8.parse(key);
	iv = CryptoJS.enc.Utf8.parse(iv);

	srcs = CryptoJS.enc.Utf8.parse(word);
	var encrypted = CryptoJS.AES.encrypt(srcs, key, {
		iv: iv,
		mode: CryptoJS.mode.CBC,
		padding: CryptoJS.pad.Pkcs7
	});
	return encrypted.toString();
}


var jsCommon = {
	//根据参数名获取参数值
	getQueryString: function (name) {
		var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
		var r = window.location.search.substr(1).match(reg);

		if (r != null) return unescape(r[2]);
		return "";
	},

	//替换系统的alert
	alert: function (msg) {
		if (typeof (layer) == "undefined") {
			alert(msg);
		} else {
			layer.alert(msg, {
				title: _sys_title,
				time: 10000
			});
		}
	},

	alert_close: function (msg, not_reload_flag) {
		if (typeof (layer) == "undefined") {
			alert(msg);
			window.close();
		} else {
			layer.alert(msg, {
				title: _sys_title,
				time: 3000
			},
				function () {
					if (!not_reload_flag) {
						parent.location.reload();
					}
					parent.layer.closeAll();
				}
			);
		}
	},

	alert_redirct: function (msg, url) {
		if (typeof (layer) == "undefined") {
			alert(msg);
			window.location = url;
		} else {
			layer.alert(msg, {
				title: _sys_title,
				time: 3000
			},
				function () {
					if (url.substr(0, 11).toLowerCase() == "javascript:") {
						eval(url.substr(11));
					} else {
						window.location = url;
					}
				}
			);
		}
	},

	//询问提示框
	confirm: function (msg, callback1, callback2) {
		if (typeof (layer) == "undefined") {
			if (confirm(msg)) {
				callback1;
			} else {
				callback2;
			}
		}

		var result = false;

		layer.confirm(msg, {
			title: _sys_title,
			btn: ['确定', '取消'] //按钮
		},
			callback1,
			callback2
		);
	},

	//增加某条数据
	addTableData: function (table_code, params) {
		var url = "?_module=settings&_name=Table&_do=tabledata_add&table_code=" + table_code + "&" + params;
		window.open(url);
	},

	//修改某条数据
	editTableData: function (table_code, pk_id, params) {
		var url = "?_module=settings&_name=Table&_do=tabledata_add&actiontype=edit&table_code=" + table_code + "&pk_id=" + pk_id + "&group_code=" + params;
		window.open(url);
	},

	//删除某条数据
	deleteTableData: function (table_code, pk_id, bus_table_code, bus_pk_id) {

		if (confirm("请问您确定要删除本条数据吗？删除后不可恢复")) {
			$.ajax({
				type: 'GET',
				url: _sys_baseurl + '/Data/Delete?dataname=' + table_code + "&pk_id=" + pk_id
					+ "&bus_table_code=" + bus_table_code + "&bus_pk_id=" + bus_pk_id,
				async: true,
				success: function (data) {
					var uri = location.pathname + window.location.search;
					if (data.result == "1") {
						alert('删除成功！');
						window.location = uri;
					} else {
						alert(data.err_msg);
					}
				},
			});
		}
	},

	//删除多条数据
	deleteTableDataBatch: function () {
		var dataname = jsCommon.getQueryString("dataname");
		if (confirm("请问您确定要删除多条数据吗？删除后不可恢复！")) {
			$chkItem = $("input[name='chk_sel[]']:checked");
			if ($chkItem.length > 0) {
				var id_array = new Array();

				$chkItem.each(function () {
					id_array.push($(this).val());
				});
				var idstr = id_array.join(',');

				$.ajax({
					type: 'GET',
					url: _sys_baseurl + '/Data/Delete?dataname=' + dataname
						+ "&pk_id=" + idstr,
					async: false,
					success: function (data) {
						var uri = location.pathname + window.location.search;
						if (data.result == "1") {
							alert('删除成功！');
							window.location = uri;
						} else {
							alert(data.err_msg);
						}
					},
				});
			}
		}
	},

	//导入
	importTableData: function (table_code, params) {
		var url = "?_module=settings&_name=Table&_do=tabledata_import&table_code=" + table_code + "&" + params;
		window.open(url);
	},

	//批量操作
	batchTableData: function (table_code, event, values) {
		$chkItem = $("input[name=chkItem]:checked");
		if ($chkItem.length > 0) {
			var id_array = new Array();

			$chkItem.each(function () {
				id_array.push($(this).val());
			});
			var idstr = id_array.join(',');

			var url = "?_module=settings&_name=Table&_do=Chosen&actiontype=edit&table_code=" + table_code + "&event=" + event + "&values=" + values + "&idstr=" + idstr;
			window.open(url);
		}
	},

	goto_login: function () {
		if (window.parent) {
			if (window.parent.parent) {
				window.parent.parent.location = "?_do=login";
			} else {
				window.parent.location = "?_do=login";
			}
		} else {
			window.location = "?_do=login";
		}
	},

	//下载电子档案
	downloadEfile: function (archive_efile_id) {
		window.open(_sys_baseurl + "/EFile/Download?archive_efile_id=" + archive_efile_id);
	},

	//查看电子档案
	viewEfile: function (archive_efile_id) {
		window.open(_sys_rootpath + "/module/bussines/efileview/view.html?archive_efile_id=" + archive_efile_id);
	},

	//删除电子档案
	deleteEfile: function (archive_efile_id, sussfunctype) {
		if (confirm("删除后不可恢复,请问您确定要删除该电子档案吗？")) {
			$.ajax({
				type: 'GET',
				url: _sys_baseurl + "/EFile/Delete?archive_efile_id=" + archive_efile_id,
				async: true,
				success: function (data) {
					if (data.result == "1") {
						alert('删除成功！');
						if (sussfunctype == "1") {
							window.location = uri;
						} else {
							browseEfile(last_list_type_id, last_list_flag);
						}
					} else {
						alert(data.err_msg);
					}
				},
			});
		}
	},

	//查看实体档案
	viewEntity: function (archive_entity_id) {
		window.open(_sys_rootpath + "/dataadd.html?dataname=erms_archive_entity&actiontype=view&pk_id=" + archive_entity_id);
	},

	//添加收藏
	addFavEfile: function (archive_efile_id) {
		$.ajax({
			url: _sys_baseurl + "/EFile/AddFav?archive_efile_id=" + archive_efile_id, success: function (result) {
				if (result.result == "1") {
					jsCommon.alert("添加到收藏夹成功！");
				} else {
					jsCommon.alert(data_info.err_msg);
				}
			}
		});
	},

	//搜索
	searchAll: function (search_key) {
		window.open("searchlist.html?search_key=" + search_key);
	},

	//判断是否存在于数组中
	in_array: function (find, arr) {
		for (var i = 0; i < arr.length; i++) {
			if (find == arr[i]) {
				return true;
			}
		}

		return false;
	},

	//do_userroleset
	do_userroleset: function (userid) {
		var url = _sys_rootpath + "/module/sys/userroleset.html?userid=" + userid;
		window.open(url);
	}
	//
	/*锁定
	lockPage: function() {
		alert("系统检测到您长时间未操作，需要您重新登录。");
		jsCommon.goto_login();
	}
	*/
}

$.ajaxPrefilter(function (options, originalOptions, jqXHR) {
	// 添加一个全局的请求头
	options.headers = options.headers || {};
	//options.headers['Access-Token'] = sessionStorage.getItem('kempToken');

	// 如果你想取消请求，可以返回false
	// return false;
});

function formaDate(timer, type) {
	let year = timer.getFullYear();
	let month = timer.getMonth() + 1; // 由于月份从0开始，因此需加1
	if (month < 10) month = "0" + month;
	let day = timer.getDate();
	if (day < 10) day = "0" + day;
	if (type === 'date') return `${year}-${month}-${day}`;
	let hour = timer.getHours();
	if (hour < 10) hour = "0" + hour;
	let minute = timer.getMinutes();
	if (minute < 10) minute = "0" + minute;
	let second = timer.getSeconds();
	if (second < 10) second = "0" + second;
	return `${year}-${month}-${day} ${hour}:${minute}:${second}`;
}