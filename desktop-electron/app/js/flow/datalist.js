var urlParams = new URLSearchParams(window.location.search);
var flow_node_id = urlParams.get('flow_node_id');
var arr = [];

function fpnr(s) {
    xmxz = ["电网建设", "主网建设", "配网建设", "小型基建", "技改", "修理", "科技", "信息化", "职创", "其他"];
    fpnr_list = [
        [],
        [],
        [],
        [],
        ["初设评审费", "可研费", "招标代理费", "监理费", "设计费", "结算审核费", "进度款", "结算款", "可研评审费"],
        ["预算编制费", "预算审核费", "可研费", "可研评审费", "进度款", "监理费", "结算款", "结算审核费"],
        ["版面设计费", "专利费", "软件著作权申请代理费", "测试费", "专家咨询费", "文印费", "图书费", "技术开发费"],
        [],
        ["版面设计费", "专利费", "软件著作权申请代理费", "测试费", "专家咨询费", "文印费", "图书费"],
        []
    ];
    str = "<option value></option>";

    for (i = 0; i < xmxz.length; i++) {
        if (xmxz[i] == s) {
            for (y = 0; y < fpnr_list[i].length; y++) {

                str += "<option value='" + fpnr_list[i][y] + "'>" + fpnr_list[i][y] + "</option>";
            }
        }
    }
    $("#find_f_fpnrr").html(str);
}

if ($("#dataname").val() == "FPFHGL") {
    $('#find_f_xmxz').change(function () {
        fpnr($('#find_f_xmxz').val());
    });
}

fpnr($('#find_f_xmxz').val());

//alert(new URLSearchParams(window.location.search).get("dataname"));
var dataname_list = ['CZXX_DDBHGL', 'CZXX_ZDHCZDZ', 'ZDHGJ_ALL', 'PWGJ_ALL'];
for (i = 0; i < dataname_list.length; i++) {
    if (new URLSearchParams(window.location.search).get("dataname") == dataname_list[i]) {
        $(".mb-3").eq(0).hide();
        break;
    }
}
// if ($("#dataname").val() == "CZXX_DDBHGL" || $("#dataname").val() == "CZXX_ZDHCZDZ") {
//     $(".mb-3").eq(0).hide();
// }

if (new URLSearchParams(window.location.search).get("dataname") == "ZDHQXCLJL") {
    const trs = document.getElementsByTagName('tr');

    // 遍历每一行
    for (let tr of trs) {
        // 获取当前行的第3个td（索引为2，因为从0开始）
        const tds = tr.getElementsByTagName('td');
        if (tds.length >= 3) { // 确保至少有3个td
            tds[2].style.width = '300px'; // 设置宽度
        }
    }
}


// 自动化厂站定值隐藏当前处理人
if (flow_node_id == '10001405') {
    // 遍历所有具有 row-sticky 类的行
    $(".row-sticky").each(function () {
        // 获取当前行
        var $row = $(this);

        // 获取最后一列的文本内容
        var lastCellText = $row.find("td:last-child div").text();
        // 检查是否为"已执行"
        if (lastCellText.trim() == "已执行") {
            // 将倒数第二列的div内容清空
            $row.find("td:nth-last-child(2) div").text("主站自动化");
        } else {
            $row.find("td:nth-last-child(2) div").text($row.find("td:nth-last-child(2) div").text().trim() + "主站自动化");
        }
    });
}

//负荷预测屏蔽导出功能按钮
if (new URLSearchParams(window.location.search).get("dataname") == "OMS_FHYC_DPS") {
    // 隐藏"导出当前页"按钮
    var exportCurrentBtn = document.querySelector('input[value="导出当前页"]');
    if (exportCurrentBtn) {
        exportCurrentBtn.style.display = 'none';
    }

    // 隐藏"导出全部"按钮
    var exportAllBtn = document.querySelector('input[value="导出全部"]');
    if (exportAllBtn) {
        exportAllBtn.style.display = 'none';
    }
}

if (new URLSearchParams(window.location.search).get("dataname") == "OMS_JZB_FHYC") {
    // 隐藏"导出当前页"按钮
    var exportCurrentBtn = document.querySelector('input[value="导出当前页"]');
    if (exportCurrentBtn) {
        exportCurrentBtn.style.display = 'none';
    }

    // 隐藏"导出全部"按钮
    var exportAllBtn = document.querySelector('input[value="导出全部"]');
    if (exportAllBtn) {
        exportAllBtn.style.display = 'none';
    }
}

function openFanKuiModal() {
    arr = [];
    $("input[name='chk_sel[]']:checked").each(function () {
        arr.push($(this).val());
    });
    if (arr == "") {
        alert("请勾选至少一条告警数据！");
        return;
    }
    $.ajax({
        async: true,
        url: _sys_baseurl + "/Zwdd/duplicate?idList=" + arr,
        success: function (result) {
            if (result.gjxx == 0) {
                alert("请勾选告警内容一样的数据！");
                return;
            }
            var GJLX = "事故";
            if (result.gjxx[0].GJDJ <= 19) {
                GJLX = "越限";
            } else if (result.gjxx[0].GJDJ > 19 && result.gjxx[0].GJDJ <= 21) {
                GJLX = "异常";
            }
            $("#CZ").val(result.gjxx[0].CZ);
            $("#GJNR").val(result.gjxx[0].GJNR);
            $("#GJLX").val(GJLX);
            $('#FanKuiModal').modal('show');
        }
    });

}


function submitFeedback() {
    const JCR = $('#JCR').val().trim();
    const JCSJ = $('#JCSJ').val().trim();
    const XHJCJG = $('#XHJCJG').val().trim();
    const FSYY = $('#feedbackFSYY').val();
    const JCJGFL = $("#JCJGFL").val();
    // 验证
    if (!JCR) {
        alert('请输入检查人！');
        $('#JCR').focus();
        return;
    }
    if (!JCSJ) {
        alert('请选择检查时间！');
        $('#JCSJ').focus();
        return;
    }
    if (!XHJCJG) {
        alert('请输入信号检查结果！');
        $('#XHJCJG').focus();
        return;
    }
    // if (!FSYY) {
    //     alert('请选择发生原因！');
    //     $('#feedbackFSYY').focus();
    //     return;
    // }
    if (!JCJGFL) {
        alert('请选择检查结果分类！');
        $('#JCJGFL').focus();
        return;
    }
    const dataId = arr.join(',');
    //信息回填
    $.ajax({
        type: "POST",
        url: _sys_baseurl + "/Zwdd/GjhtxxPlfk",
        data: {
            dataId: dataId,
            JCR: JCR,
            JCSJ: JCSJ,
            XHJCJG: XHJCJG,
            FSYY: FSYY,
            JCJGFL: JCJGFL
        },
        success: (res) => {
            alert("反馈成功！请刷新页面。。。。");
            $('#FanKuiModal').modal('hide');
            // 清空勾选
            this.checkedIds = [];
            // 如果需要，重新加载列表
            this.getList();
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