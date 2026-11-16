
//点击提交
// $('#tijiao').click(function(){
// $('[name="tijiao"]').click(function(){
//调度编号图状态修改
function tijiao() {
    if ($("#flow_node_id").val() == '10000809') {
        var xhr = new XMLHttpRequest();
        url = _sys_baseurl + "/Ajax/Ddbht?dataId=" + $("#dataId").val();
        xhr.open("GET", url, false);
        var str;
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4 && xhr.status === 200) {
                str = true;
            }
            else {
                str = false;
            }
        };
        xhr.send();
        return str;
    }

    //调度编号图管理状态修改为待处理
    if ($("#flow_node_id").val() == '10000804') {
        $.ajax({
            async: true,
            url: _sys_baseurl + "/Ajax/DdbhDzx?id=" + $("#dataId").val()
        });
    }

    //断面及线路限流作废处理
    if ($("#flow_node_id").val() == '10000501' || $("#flow_node_id").val() == '10000601' || $("#flow_node_id").val() == '10000401' || $("#flow_node_id").val() == '10000701') {
        F_ZFBBH = $("#F_ZFBBH").val();
        $.ajax({
            async: true,
            url: _sys_baseurl + "/Ajax/Zfcl?zfbbh=" + F_ZFBBH + "&flow_node_id=" + $("#flow_node_id").val()
        });
    }

    //自动化厂站定值状态处理
    if ($("#flow_node_id").val() == '10001405' || $("#flow_node_id").val() == '10001406' || $("#flow_node_id").val() == '10001404') {
        //if ($("#flow_node_id").val() == '10001405' || $("#flow_node_id").val() == '10001406') {
        $.ajax({
            async: true,
            url: _sys_baseurl + "/Ajax/CzdzZtcl?id=" + $("#dataId").val() + "&flow_node_id=" + $("#flow_node_id").val()
        });
    }

    //母线正常运行方式
    if ($("#flow_node_id").val() == '10000104') {
        $.ajax({
            async: true,
            url: _sys_baseurl + "/Ajax/Mxzcyxfs?dataId=" + $("#dataId").val()
        });
    }
}

//回退
$('[name="huitui"]').click(function () {
    //回退时清空保存的信息
    var flow_node_id = ['10000205', '10000305', '10000403', '10000503', '10000603', '10000703', '10000803', '10000808', '10001301', '10001303',
        '10001305', '10001405', '10001505', '10005101', '10005102', '10005105', '10005201', '10005406', '10005506', '10005802', '10005805', '10005903'];
    for (i = 0; i < flow_node_id.length; i++) {
        if ($("#flow_node_id").val() == flow_node_id[i]) {
            $.ajax({
                async: true,
                url: _sys_baseurl + "/Ajax/Clear?flow_node_id=" + flow_node_id[i] + "&dataId=" + $("#dataId").val()
            });
            break;
        }
    }
    //自动化厂站定值回退状态处理
    // if ($("#flow_node_id").val() == '10001405' || $("#flow_node_id").val() == '10001406') {
    //     $.ajax({
    //         async: true,
    //         url: _sys_baseurl + "/Ajax/CzdzHt?id=" + $("#dataId").val()
    //     });
    // }
})




//调度编号图控制巡维中心是否必填
function xwzx() {
    if ($('#flow_node_id').val() == '10000805') {
        if ($('[fieldname="F_SSLX"]').text() == "厂站图") {
            if ($('#F_SFXBDQR').val() == '否') {
                alert("厂站图必须发给变电");
                return true;
            }
        }
        if ($('#F_SFXBDQR').val() == '是') {
            if ($('#DEPARTMENTS').val() == "" || $('#DEPARTMENTS').val() == null) {
                //if ($('[fieldname="F_SSLX"]').text() == "厂站图" && ($('#DEPARTMENTS').val() == "" || $('#DEPARTMENTS').val() == null)) {
                alert("巡维中心不能为空");
                return true;
            }
        }
    }
}

//自动化厂站定值
if ($("#flow_node_id").val() == '10001401') {
    $('#F_BZR').change(function () {
        if ($("#loginName").val() != "") {
            if ($('#F_BZR').val() != $("#loginName").val()) {
                $('#F_BZR').val($("#loginName").val());
                alert("编制人名称要跟登录人名称一样");
            }
        }
    });

    //闭锁控制
    var $bs = $('#F_LX,#F_BZSJ,#F_JHYSKSRQ,#_app_');
    $bs.on('click', function () {
        lx = $('#F_LX').val();
        kssj = $('#F_BZSJ').val();
        jssj = $('#F_JHYSKSRQ').val();
        //alert(Date.parse($('#F_JHYSKSRQ').val()))
        if (jssj != "") {
            if (lx == null && (kssj != "" || jssj != "")) {
                $('#F_JHYSKSRQ').val("");
                alert("请选择类型后再编辑计划验收开始时间");
            } else if (lx != null && kssj == "" && jssj != "") {
                $('#F_JHYSJSRQ').val("");
                alert("请选择编辑时间后再选择计划验收开始时间");
            } else if (lx != null && kssj != "" && jssj != "") {
                kssj_linx = Date.parse(kssj);
                jssj_linx = Date.parse(jssj);
                if (lx == "基建站") {
                    if (jssj_linx - kssj_linx < 2073600000) {
                        $('#F_JHYSKSRQ').val("");
                        alert("编辑时间与计划验收开始时间相差不够24天，请正确填写计划验收开始时间")
                    }
                } else {
                    if (jssj_linx - kssj_linx < 1036800000) {
                        $('#F_JHYSKSRQ').val("");
                        alert("编辑时间与计划验收开始时间相差不够12天，请正确填写计划验收开始时间")
                    }
                }
            }

        }
    });
    //document.getElementById('F_ZFBXH').readOnly = true;
    var $fields = $('#F_CZMC, #F_ZFBXH');
    $fields.on('change', function () {
        xxbh()
    });
}

var $fields_click = $('[name="tijiao"]')
$fields_click.on('click', function () {
    xxbh();
});

//自动化厂站定值信息编号控制
function xxbh() {
    if ($("#flow_node_id").val() == '10001401') {
        //$('#F_CZMC').change(function(){
        var CZMC = $('#F_CZMC').val().replace(/[^\u4E00-\u9FA5]/g, '');
        CZMC = CZMC.replace("变电站", "").replace("站", "");
        $.ajax({
            async: true,
            url: _sys_baseurl + "/Ajax/Czdz?czmc=" + CZMC + "&dataId=" + $("#dataId").val(),
            success: function (result) {
                var zfbxh = result.data.zfbxh;
                //alert(zfbxh);
                var num = "001";
                if (zfbxh == 1) {
                    $("#F_ZFBXH").val("");
                    alert("当前厂站名称有单据还未通过审核，请待厂站审核后再起新的单据");
                } else if (zfbxh != "") {
                    num = zfbxh.slice(-3);
                    if (Number.isFinite(parseInt(num, 10))) {
                        num = parseInt(num) + 1;
                        num = num < 10 ? "00" + num : num <= 99 && num >= 10 ? "0" + num : num;
                    } else {
                        num = "001";
                    }
                    $('#F_ZFBXH').val(CZMC + "变电站-" + num);
                } else {
                    $('#F_ZFBXH').val(CZMC + "变电站-" + num);
                }

            }
        });
    }
}

//调度编号图编制节点
if (['10000801', '10006201'].includes($("#flow_node_id").val())) {
    //调度编号图修改AI审核字段权限
    $('#AISH').prop('disabled', true)
        .removeAttr('placeholder')  // 删除提示文字
        .css({
            'background-color': 'transparent',  // 透明背景
            'background-image': 'none',         // 删除背景图片
            'border': '1px solid transparent',  // 透明边框
            'cursor': 'not-allowed',            // 禁用光标
            'resize': 'none',                   // 禁止调整大小
            'opacity': '0.7',                   // 半透明效果
            'user-select': 'none',              // 禁止选择文字
            'box-shadow': 'none'                // 删除阴影
        });
}

//调度编号图编制节点
if ($("#flow_node_id").val() == '10000801') {
    //根据编号生成图档名称
    $('#F_BZFJ').change(function () {
        var fjmc = $('#F_BZFJ').val();
        fjmc = fjmc.split("\\");
        fjmc = fjmc[fjmc.length - 1].split(".")[0];
        $("#F_TDMC").val(fjmc);
    });
}


//调度编号图
/*
if ($("#flow_node_id").val() == '10000805') {
    $('#F_SSLX').change(function () {
        if ($('#F_SFXBDQR').val()=="是") {
            $("#DEPARTMENTS").show();
        } else {
            $("#DEPARTMENTS").val("");
            $("#DEPARTMENTS").hide();
        }
    });

    $('#F_CZM').change(function () {
        var CZM = $('#F_CZM').val().replace(/[^\u4E00-\u9FA5]/g, '');
        $.ajax({
            async: true,
            url: _sys_baseurl + "/Ajax/DdbhXwzx?czm=" + CZM,
            contentType: 'application/x-www-form-urlencoded;charset=UTF-8',
            success: function (result) {
                if (result.data.XWZX == "") {
                    alert("根据填写的厂站没有搜索到对应的巡维中心，请确认厂站名是否正确或手动选择巡维中心");
                } else {
                    $("#DEPARTMENTS").val(result.data.XWZX);
                }
                console.log(result.data.XWZX);
            }
        });
    });


    $('#F_SSLX').change(function () {
        if ($.trim($('#F_SSLX').val()) == "厂站图") {
            $("#F_CZM").attr("required", true);
        } else {
            $("#F_CZM").removeAttr("required");
        }
    });
}
*/
//自动化缺陷处理
if ($("#flow_node_id").val() == '10005101') {
    deptId = $("#deptId").val();
    //alert(deptId)
    //$('#ZY').attr('disabled', true);
    $("#ZY").val(deptId == '27790' || deptId == '127720' ? "主网" : deptId == "25157" ? "配网" : "");
}

//线路重合闸检修单计算时长
function get_LinxTime(time1, time2) {
    linxTime1 = Date.parse(time1);
    linxTime2 = Date.parse(time2);
    linxTime3 = (linxTime2 - linxTime1) / 1000
    D = Math.floor(linxTime3 / 86400);
    H = Math.floor((linxTime3 % 86400) / 3600)
    M = Math.floor(((linxTime3 % 86400) % 3600) / 60)
    return D + "天" + H + "小时" + M + "分钟"
}

if ($("#flow_node_id").val() == '10001001') {
    //调佣计算时长方法
    $('#F_SQGZSZ').click(function () {
        $('#F_SQGZSZ').val(get_LinxTime($("#F_SQGZKSSJ").val(), $("#F_SQGZJSSJ").val()));
    })
    $('#F_SQZXECAQCSCXSJ').click(function () {
        $('#F_SQZXECAQCSCXSJ').val(get_LinxTime($("#F_SQZXECAQCSSJ").val(), $("#F_SQHFECAQCSSJ").val()));
    })
}

if ($("#flow_node_id").val() == '10001002') {
    //默认10001001节点的时间值
    $('#F_PZGZSZ').val($('#F_PZGZSZ').val() == "" ? $('#F_SQGZSZ').val() : $('#F_PZGZSZ').val());
    $('#F_PZGZKSSJ').val($('#F_PZGZKSSJ').val() == "" ? $('#F_SQGZKSSJ').val() : $('#F_PZGZKSSJ').val());
    $('#F_PZGZJSSJ').val($('#F_PZGZJSSJ').val() == "" ? $('#F_SQGZJSSJ').val() : $('#F_PZGZJSSJ').val());
    $('#F_PZZXECAQCSCXSZ').val($('#F_PZZXECAQCSCXSZ').val() == "" ? $('#F_SQZXECAQCSCXSJ').val() : $('#F_PZZXECAQCSCXSZ').val());
    $('#F_PZHFECAQCSSJ').val($('#F_PZHFECAQCSSJ').val() == "" ? $('#F_SQHFECAQCSSJ').val() : $('#F_PZHFECAQCSSJ').val());
    $('#F_PZZXECAQCSSJ').val($('#F_PZZXECAQCSSJ').val() == "" ? $('#F_SQZXECAQCSSJ').val() : $('#F_PZZXECAQCSSJ').val());
    //调佣计算时长方法
    $('#F_PZGZSZ').click(function () {
        $('#F_PZGZSZ').val(get_LinxTime($("#F_PZGZKSSJ").val(), $("#F_PZGZJSSJ").val()));
    })
    $('#F_PZZXECAQCSCXSZ').click(function () {
        $('#F_PZZXECAQCSCXSZ').val(get_LinxTime($("#F_PZZXECAQCSSJ").val(), $("#F_PZHFECAQCSSJ").val()));
    })
}

////////////////////////////////////////////////
//110kV地调输电设备限流管理
//根据电压等级：来显示与隐藏不同的分节
if ((jsCommon.getQueryString("flow_code") == "SDSBXL110KV") ||
    ($("#flow_node_id").val().substr(0, 6) == "100002")) {
    var objtmp = document.getElementById("F_DYDJ");

    if (objtmp) {
        objtmp.onchange = selectDydjOfDSBXL110KV;
        selectDydjOfDSBXL110KV();
    } else {
        objtmp = $("[fieldname='F_DYDJ']:first");

        if (objtmp != null) {
            selectDydjOfDSBXL110KVImpl(objtmp.html());
        }
    }
}

function selectDydjOfDSBXL110KV() {
    var dydj = $("#F_DYDJ").val();
    selectDydjOfDSBXL110KVImpl(dydj);
}

function selectDydjOfDSBXL110KVImpl(dydj) {
    $("div").each(function () {
        var dydj_curr = $(this).attr("group_remark");
        if (dydj_curr != undefined && dydj_curr != "") {
            if (dydj_curr.includes(dydj)) {
                $(this).css("display", "");
            } else {
                $(this).css("display", "none");
            }
        }
    });
}
////////////////////////////////////////////////

if ($("#flow_node_id").val() == '10001401') {
    xxbh();
    $("#F_BGYY").attr('placeholder', "请填写信息表变更内容");
    $("#F_ZFBXH").attr('required', "true");
}


var urlParams = new URLSearchParams(window.location.search);
var paramValue = urlParams.get('flow_code');
if (paramValue == "ZDHQXCLJL") {

    const items = document.querySelectorAll('.ddbhgl-ul-li');
    items.forEach(item => {
        if (item.textContent.includes('节点控制(别动)')) {
            item.style.display = 'none';
        }
    });

}

//告警信息回填
$('#JCJGFL').change(function () {
    $("#XHJCJG").val($('#JCJGFL').val());
});

$('#XHJCJG').change(function () {
    if ($('#JCJGFL').val() == "") {
        alert("请先填写检查结果分类再填写信号检查结果");
        $('#XHJCJG').val("");
    }
});

//用印申请默认申请部门
if ($("#flow_node_id").val() == '4028932701') {
    $("#SQBM").val($("#DepartmentId").val());
}

//核心场所授权天数控制
if ($("#flow_node_id").val() == '10006601') {
    //页面加载完如果选择外协人员则删除最后一个选项
    if ($("#YRLX").val() == '外协人员') {
        $("select[name='SQTS']").each(function () {
            let $sel = $(this);
            $sel.find("option:last").remove();
        });
    }

    //点击按钮如果外协人员则删除最后一行的最后一个选择
    $('.btn.btn-primary').on('click', function () {
        if ($("#YRLX").val() == '外协人员') {
            $("select[name='SQTS']").last().find("option:last").remove();
        }
    })

    //修改YRLX选项发生变化时
    $(function () {
        function handleYrlx() {
            let yrlxVal = $("#YRLX").val();

            $("select[name='SQTS']").each(function () {
                let $sel = $(this);
                // 找到文本=长期的选项
                let $optChangQi = $sel.find("option").filter(function () {
                    return $(this).text().trim() === "长期";
                });

                if (yrlxVal === "正式员工") {
                    // 正式员工：不存在长期则追加
                    if ($optChangQi.length === 0) {
                        $sel.append('<option value="长期">长期</option>');
                    }
                } else if (yrlxVal === "外协人员") {
                    // 外协人员：删掉【长期】选项
                    if ($optChangQi.length > 0) {
                        $optChangQi.remove();
                    }
                }
            });
        }

        handleYrlx(); //页面加载初始化执行
        $("#YRLX").on("change", handleYrlx);
    });
}

/////////////////////////////////////////////////////////////
//核心场所导入EXCEL功能
var IMPORT_CONFIG = {
    tableId: '100067',        // 表格 table_id
    tableName: 'subtable',     // 表格 name
    rowDelay: 120              // 每行处理间隔(ms)
};

function importExcel(input) {
    var tablesubInfo = window.currentExcelTableInfo;

    if (!tablesubInfo) {
        alert('没有找到当前导入的子表');
        return;
    }

    var file = input.files[0];

    if (!file) {
        alert('没有选中文件');
        return;
    }
    var reader = new FileReader();

    reader.onload = function (e) {
        try {
            var data = new Uint8Array(e.target.result);
            var workbook = XLSX.read(data, { type: 'array' });

            var rows = XLSX.utils.sheet_to_json(workbook.Sheets[workbook.SheetNames[0]], {
                header: 1, defval: ''
            });

            if (rows.length < 2) {
                alert('Excel中没有数据行（第一行应为表头）');
                input.value = '';
                return;
            }

            // 收集非空数据行
            var dataRows = [];
            for (var i = 1; i < rows.length; i++) {
                if (rows[i] && rows[i].some(function (c) { return c !== '' && c != null; })) {
                    dataRows.push(rows[i]);
                }
            }

            if (dataRows.length === 0) {
                input.value = '';
                return;
            }

            // 逐行导入
            processRows(dataRows, 0, tablesubInfo, function () {
                alert('导入完成，共 ' + dataRows.length + ' 行');
            });

        } catch (err) {
            alert('Excel解析失败：' + err.message);
        }
        input.value = '';
    };
    reader.onerror = function () { alert('文件读取失败'); };
    reader.readAsArrayBuffer(file);
}
// 增加一个 Cascader 查找函数821
function findCascaderPath(options, targetLabel, parentPath) {

    if (!Array.isArray(options)) {
        return [];
    }

    parentPath = parentPath || [];

    for (var i = 0; i < options.length; i++) {

        var item = options[i];

        var currentPath = parentPath.concat(item.value);

        if (
            String(item.label).trim() ===
            String(targetLabel).trim()
        ) {
            return currentPath;
        }

        if (
            Array.isArray(item.children) &&
            item.children.length
        ) {

            var result = findCascaderPath(
                item.children,
                targetLabel,
                currentPath
            );

            if (result.length) {
                return result;
            }
        }
    }

    return [];
}
//增加 Excel 文字转 Cascader value
function convertCascaderText(text, options) {

    if (!text || !Array.isArray(options)) {
        return [];
    }

    var result = [];

    var labels = String(text)
        .split(',')
        .map(function (item) {
            return item.trim();
        })
        .filter(function (item) {
            return item !== '';
        });

    labels.forEach(function (label) {

        var path = findCascaderPath(
            options,
            label,
            []
        );

        if (path.length) {
            result.push(path);
        } else {
            console.warn(
                '【Excel】Cascader找不到：',
                label
            );
        }
    });

    return result;
}

function processRows(dataRows, index, tablesubInfo, done) {

    if (index >= dataRows.length) {
        done();
        return;
    }

    var rowData = dataRows[index];

    console.log('【导入】Excel第' + (index + 1) + '行：新增行');

    // 使用 Vue 新增行方法 在flowform.html内
    window.excelAddNewTr(
        tablesubInfo
    );
    setTimeout(function () {
        var tbody = document.querySelector(
            'table[name="subtable"] tbody'
        );

        if (!tbody) {
            console.error('【导入】没有找到 tbody');
            return;
        }

        var trList = tbody.querySelectorAll('tr');

        var lastRow = trList[trList.length - 1];

        if (!lastRow) {
            console.error('【导入】新增行失败');
            return;
        }

        fillRowData(lastRow, rowData);

        setTimeout(function () {
            processRows(
                dataRows,
                index + 1,
                tablesubInfo,
                done
            );
        }, IMPORT_CONFIG.rowDelay);
    }, 80);
}

function addSubTableRow() {
    var objtable = document.querySelector('table[table_id="' + IMPORT_CONFIG.tableId + '"]');
    if (!objtable || objtable.rows.length < 2) return;

    var srctr = objtable.rows[1];
    var newtr = document.createElement("tr");
    for (var i = 0; i < srctr.cells.length; i++) {
        var newtd = document.createElement("td");
        newtd.innerHTML = srctr.cells[i].innerHTML;
        newtr.appendChild(newtd);
    }
    objtable.getElementsByTagName("tbody")[0].appendChild(newtr);
}

function fillRowData(targetRow, rowData) {
    if (!targetRow) {
        console.error('fillRowData: targetRow 为空');
        return;
    }

    setInputValue(targetRow, 'XM', rowData[0]);      // 姓名
    setInputValue(targetRow, 'SFZH', rowData[1]);    // 身份证号
    setInputValue(targetRow, 'SSGS', rowData[2]);    // 所属公司
    setInputValue(targetRow, 'LXFS', rowData[3]);    // 联系方式

    var sqnrText = rowData[4] != null ? String(rowData[4]).trim() : '';
    console.log('【导入】SQNR Excel值：', sqnrText);

    var sqnrHidden = targetRow.querySelector(
        'input[name="SQNR"][type="hidden"]'
    );
    if (sqnrHidden) {
        if (sqnrText) {
            sqnrHidden.setAttribute(
                'data-excel-text',
                sqnrText
            );
        } else {
            sqnrHidden.removeAttribute(
                'data-excel-text'
            );
        }
    }

    // if (sqnrHidden) {
    //     if (sqnrText) sqnrHidden.setAttribute('data-excel-text', sqnrText);
    //     else sqnrHidden.removeAttribute('data-excel-text');
    // }

    setSelectValue(targetRow, 'SQTS', rowData[5]);   // 授权天数
    setSelectValue(targetRow, 'FZGRQX', rowData[6]); // 非工作日权限

    console.log('【导入】行写入完成：', rowData);
}

function setInputValue(row, name, value) {
    var input = row.querySelector('input[name="' + name + '"]');
    if (input) input.value = value != null ? String(value) : '';
}

function setSelectValue(row, name, value) {
    if (value == null || value === '') return;
    var select = row.querySelector('select[name="' + name + '"]');
    if (!select) return;
    var val = String(value).trim();
    for (var i = 0; i < select.options.length; i++) {
        if (select.options[i].value === val || select.options[i].text === val) {
            select.selectedIndex = i; return;
        }
    }
    for (var j = 0; j < select.options.length; j++) {
        if (select.options[j].value.indexOf(val) === 0) {
            select.selectedIndex = j; return;
        }
    }
}

// 下载Excel模版
function downloadExcelTemplate() {
    var link = document.createElement('a');

    link.href = 'file/门禁导入模版.xlsx';
    link.download = '门禁导入模版.xlsx';

    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}

// 提交时验证表格里的内容
function validateSubTable() {
    var table = document.querySelector('table[name="subtable"]');

    if (!table) {
        return true;
    }

    var rows = table.querySelectorAll('tbody tr');

    for (var i = 0; i < rows.length; i++) {
        var tr = rows[i];
        var rowNo = i + 1;

        // 姓名
        var xm = getRowValue(tr, 'XM');

        if (!xm) {
            alert('第 ' + rowNo + ' 行：姓名不能为空');
            return false;
        }

        // 身份证
        var sfzh = getRowValue(tr, 'SFZH');

        if (!sfzh) {
            alert('第 ' + rowNo + ' 行：身份证号不能为空');
            return false;
        }

        if (!/^\d{17}[\dXx]$/.test(sfzh)) {
            alert(
                '第 ' + rowNo +
                ' 行：身份证号必须为18位'
            );
            return false;
        }

        // 联系方式
        var lxfs = getRowValue(tr, 'LXFS');

        if (!lxfs) {
            alert('第 ' + rowNo + ' 行：联系方式不能为空');
            return false;
        }

        if (!/^1\d{10}$/.test(lxfs)) {
            alert('第 ' + rowNo + ' 行：请正确输入11位手机号');
            return false;
        }
    }
    return true;
}

function getRowValue(tr, name) {

    var input = tr.querySelector(
        'input[name="' + name + '"]'
    );

    return input ? $.trim(input.value || '') : '';
}