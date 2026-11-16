function pw_deadnumline_editpoint(obj){
	var tr = obj.parentNode.parentNode.parentNode;
	var pkid = $(tr).find("input[name='chk_sel[]']").val();
	var url=_sys_baseurl +"/Pwgj/UpdateFeederPoint?pkid="+pkid;
	
	window.open(url);
}

function pw_feeder_delete(obj){
	var tr = obj.parentNode.parentNode.parentNode;
	var pkid = $(tr).find("input[name='chk_sel[]']").val();
	
	jsCommon.deleteTableData("PWGJ_FEEDER",pkid);
}

function pw_kxzgz_querydts(obj){
	var tr = obj.parentNode.parentNode.parentNode;
	var pkid = $(tr).find("input[name='chk_sel[]']").val();
	var siteName=$(tr).find("td").eq(1).text().trim();
	var deviceName=$(tr).find("td").eq(2).text().trim();
	var startTime=$(tr).find("td").eq(6).text().trim();
	
	window.open("module/bussines/pwgj/querydts.html?site_name="
	+siteName+"&device_name="+deviceName);
}

function pw_notify_deadnumline_querydts(obj){
	var tr = obj.parentNode.parentNode.parentNode;
	var pkid = $(tr).find("input[name='chk_sel[]']").val();
	var siteName=$(tr).find("td").eq(1).text().trim();
	var deviceName=$(tr).find("td").eq(2).text().trim();
	var startTime=$(tr).find("td").eq(5).text().trim();
	
	window.open("module/bussines/pwgj/querydts.html?site_name="
	+siteName+"&device_name="+deviceName+"&start_time="+startTime);
}

//配网重合闸：人工置位
function pw_chz_dzwz_rsync(obj){
	var tr = obj.parentNode.parentNode.parentNode;
	
	var pkid = $(tr).find("input[name='chk_sel[]']").val();
	var dqwz = tr.cells[6].innerText;
	var update_wz=(dqwz=="退出")?"投入":"退出";
	if (confirm("您确定要将该线路重合闸的当前置位由【"+dqwz+"】变成【"+update_wz+"】吗？")) {
		$.ajax({
			url: _sys_baseurl + "/Pwgj/UpdateChzDqwz?chz_id="+pkid+"&update_wz="+update_wz,
			success: function(resultdata) {
				if (resultdata.result == "1") {
					alert('操作成功！');
					window.location.reload();
				} else {
					alert(data.err_msg);
				}
			}
		});
	}
}

//配网馈线死数：智能分析
function pw_deadnum_line_znfx(){
	//$('#mask').show();
	//spinner.spin($('body')[0]);
	//let target = this.event.target;
	
	window.open("module/bussines/pwgj/pwgj_deadnum_line_znfx.html");
	/*
	$.ajax({
		async: false,
		url: _sys_baseurl + "/Pwgj/DeadnumLineZnfx",
		timeout: 120 * 1000,
		success: function(result) {
			console.log(result);
			if (result.result == "1") {
				alert("智能分析成功");
			} else {
				alert("智能分析失败：" + result.err_msg);
			}

		},
		error: function(xhr, status, error) {
			alert("智能分析请求失败");
		},
		complete: function() {
			// target.
			$('#mask').hide();
			spinner.stop();
		}
	});	
	*/
}

//配网馈线死数：ELINK通知
function pw_notify_deadnumline_elink(obj) {
	$('#mask').show();
	spinner.spin($('body')[0]);
	let target = this.event.target;
	var tr = obj.parentNode.parentNode.parentNode;
	var pkid = $(tr).find("input[name='chk_sel[]']").val();
	var dataname=jsCommon.getQueryString("dataname");
	var varname=(dataname=="PW_NOTIFY_DEADNUM_LINE")?"notify_log_id":"deadnum_line_id";
	
	$.ajax({
		async: false,
		url: _sys_baseurl + "/Pwgj/DeadnumLineSendElinkOnce?"+varname+"=" + pkid,
		timeout: 120 * 1000,
		success: function(result) {
			console.log(result);
			if (result.result == "1") {
				alert("发送ELINK成功");
			} else {
				alert("发送ELINK失败：" + result.err_msg);
			}

		},
		error: function(xhr, status, error) {
			alert("发送ELINK请求失败");
		},
		complete: function() {
			// target.
			$('#mask').hide();
			spinner.stop();
		}
	});
}

//配网馈线死数告警：tel通知
function pw_notify_deadnumline_tel(obj) {
	$('#mask').show();
	spinner.spin($('body')[0]);
	let target = this.event.target;
	var tr = obj.parentNode.parentNode.parentNode;
	var pkid = $(tr).find("input[name='chk_sel[]']").val();
	var varname=(dataname=="PW_NOTIFY_DEADNUM_LINE")?"notify_log_id":"deadnum_line_id";
	
	
	$.ajax({
		async: false,
		url: _sys_baseurl + "/Pwgj/DeadnumLineCallOnce?"+varname+"=" + pkid,
		timeout: 120 * 1000,
		success: function(result) {
			console.log(result);
			if (result.result == "1") {
				alert("拔打电话成功");
			} else {
				alert("拔打电话失败：" + result.err_msg);
			}

		},
		error: function(xhr, status, error) {
			alert("拔打电话请求失败");
		},
		complete: function() {
			// target.
			$('#mask').hide();
			spinner.stop();
		}
	});
}

//配网告警：通知提示
function pw_notify_deadnumline_view(obj) {
	var datavalue = $(obj).attr("datavalue");
	layer.alert(datavalue,{skin:"oms-layer-demo-skin"});
}

//配网告警：馈线信息，清空功能
function pwgj_feeder_deleteall() {
	if (confirm("您准备清除所有10kV馈线信息数据，清除后将无法恢复。请问您要继续操作吗？")) {
		$.ajax({
			url: _sys_baseurl + "/Pwgj/DeleteAllFeeder",
			success: function(resultdata) {
				if (resultdata.result == "1") {
					alert('操作成功！');
					window.location.reload();
				} else {
					alert(data.err_msg);
				}
			}
		});
	}
}

// 拨打电话
function pwCallOnce(id) {
	$('#mask').show();
	spinner.spin($('body')[0]);
	let target = this.event.target;
	$.ajax({
		async: false,
		url: _sys_baseurl + "/Pwgj/call/" + id,
		timeout: 120 * 1000,
		success: function(result) {
			console.log(result);
			if (result.result == "1") {
				alert(result.data.msg);
			} else {
				alert("拔出电话失败" + result.err_msg);
			}
		},
		error: function(xhr, status, error) {
			alert("拔出电话请求失败");
		},
		complete: function() {
			// target.
			$('#mask').hide();
			spinner.stop();
		}
	});
}


// 配网发送ELINK消息
function pwSendElinkOnce(id) {
	$('#mask').show();
	spinner.spin($('body')[0]);
	let target = this.event.target;
	$.ajax({
		async: false,
		url: _sys_baseurl + "/Pwgj/SendElinkOnce?id=" + id,
		timeout: 120 * 1000,
		success: function(result) {
			console.log(result);
			if (result.result == "1") {
				alert("发送ELINK成功");
			} else {
				alert("发送ELINK失败：" + result.err_msg);
			}

		},
		error: function(xhr, status, error) {
			alert("发送ELINK请求失败");
		},
		complete: function() {
			// target.
			$('#mask').hide();
			spinner.stop();
		}
	});
}

function viewPwElinkInfo(obj) {
	var tr = obj.parentNode.parentNode.parentNode;

	var pkid = $(tr).find("input[name='chk_sel[]']").val();
	layer.open({
		type: 2,
		area: ['400px', '200px'],
		content: 'module/bussines/pwgj/view_elinkinfo.html?id=' + pkid
	});
}

function viewPwElinkInfoHis(obj) {
	var tr = obj.parentNode.parentNode.parentNode;

	var pkid = $(tr).find("input[name='chk_sel[]']").val();
	layer.open({
		type: 2,
		area: ['500px', '300px'],
		content: 'module/bussines/pwgj/view_elinkinfo_his.html?id=' + pkid
	});
}

////////////////////////////
//后置接口
function datalistAfterApi(dataname) {
	if (dataname == "V_SCADA_DATA_ZBBD") {
		datalistAfterApi_zbdd(dataname);
	}
}

////////////////////////////
//后置接口，主变变低日查询:datalist.html?dataname=V_SCADA_DATA_ZBBD
function datalistAfterApi_zbdd(dataname) {
	var ymd = $("#ymd").val();
	$.ajax({
		url: _sys_baseurl + "/Zwdd/QuerySbgzInfo?ymd=" + ymd + "&dataname=" + dataname,
		success: function(resultdata) {
			if (resultdata.result == "1") {
				var jsondata = resultdata.data;

				$("input[name='chk_sel[]']").each(function() {
					var scadaid = $(this).val();
					var jsonItem = jsondata[scadaid];
					var total = jsonItem.total;
					var objTr = $(this).parent().parent().parent();
					var content = jsonItem.content;
					if (content == undefined) {
						content = "";
					}
					var objTd = objTr.find("td").eq(8);
					objTd.html(objTd.html() +
						"<span class='badge bg-danger rounded-pill font-weight-normal text-white ml-2' onclick=\"jsCommon.alert('" +
						content + "')\">" + total + "</span>");

				});

			}
		}
	});
}

//基准表查询：隐藏所选
function jzb_hidesel() {
	var ids = "";
	$("input[name='chk_sel[]']").each(function() {
		var chk = $(this);
		var isChecked = chk.prop("checked");
		if (isChecked) {
			ids = (ids == "") ? chk.val() : ids + "," + chk.val();
		}
	});

	if (ids == "") {
		alert("请选择要处理的数据！");
		return;
	}

	if (!confirm("请问您确定要进行该操作吗？")) {
		return;
	}

	//请求
}
