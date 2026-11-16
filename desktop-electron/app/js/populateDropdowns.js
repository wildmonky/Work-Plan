function populateDropdowns_month() {
    var currentYear = new Date().getFullYear();
    var yearDropdown = document.getElementById('year1');

    // Populate the year dropdown with the current year and the next ten years
    for (var i = currentYear - 4; i <= currentYear + 10; i++) {
        var yearOption = document.createElement('option');
        yearOption.value = i;
        yearOption.textContent = i + '年';
        yearDropdown.appendChild(yearOption);
    }
    console.log(yearOption+"-----------------------------");
    // Populate the month dropdown with months 1 through 12
    var monthDropdown = document.getElementById('month1');
    for (var j = 1; j <= 12; j++) {
        var monthOption = document.createElement('option');
        monthOption.value = j;
        monthOption.textContent = j + '月';
        monthDropdown.appendChild(monthOption);
    }

    updateMonthDropdown();

}

function updateMonthDropdown() {
    var selectedMonth = document.getElementById('month1').value;
    var selectedYear = document.getElementById('year1').value;

    var startDayOfMonth = new Date(selectedYear, selectedMonth-1, 1);
    var lastDayOfMonth = new Date(selectedYear, selectedMonth, 0);
    //用于告警功能，将开始结束日期赋值到input
    $('input[name="startdate"]').val(formatDate(startDayOfMonth));
    $('input[name="enddate"]').val(formatDate(lastDayOfMonth));
    console.log(formatDate(startDayOfMonth)+"---")
    console.log(formatDate(lastDayOfMonth)+"---")
}

function populateDropdowns() {
    var currentYear = new Date().getFullYear();
    var yearDropdown = document.getElementById('year');

    // Populate the year dropdown with the current year and the next ten years
    for (var i = currentYear - 4; i <= currentYear + 10; i++) {
        var yearOption = document.createElement('option');
        yearOption.value = i;
        yearOption.textContent = i + '年';
        yearDropdown.appendChild(yearOption);
    }

    // Populate the month dropdown with months 1 through 12
    var monthDropdown = document.getElementById('month');
    for (var j = 1; j <= 12; j++) {
        var monthOption = document.createElement('option');
        monthOption.value = j;
        monthOption.textContent = j + '月';
        monthDropdown.appendChild(monthOption);
    }

    // Populate the week dropdown initially
    updateWeekDropdown();

    // Update the displayed information based on the initial values
    updateDateInfo();
}

function updateWeekDropdown() {
    var selectedMonth = document.getElementById('month').value;
    var selectedYear = document.getElementById('year').value;
    var weekDropdown = document.getElementById('week');
    console.log("AAAA "+ selectedMonth);
    // Clear existing options
    weekDropdown.innerHTML = '';

    // Get the number of weeks in the selected month
    var lastDayOfMonth = new Date(selectedYear, selectedMonth, 0);
    var numberOfWeeks = Math.ceil((lastDayOfMonth.getDate() + lastDayOfMonth.getDay()) / 7);

    // Populate the week dropdown with weeks and date ranges
    for (var k = 1; k <= numberOfWeeks; k++) {
        var startOfWeek = getStartOfWeek(selectedYear, selectedMonth, k);
        var endOfWeek = getEndOfWeek(selectedYear, selectedMonth, k);

        var weekOption = document.createElement('option');
        weekOption.value = k;
        weekOption.textContent = '第' + k + '周 (' + formatDate(startOfWeek) + ' ~ ' + formatDate(endOfWeek) + ')';
        weekDropdown.appendChild(weekOption);
    }

    // Call the general update function
    updateDateInfo();
}

function updateDateInfo() {
    var selectedYear = document.getElementById('year').value;
    var selectedMonth = document.getElementById('month').value;
    var selectedWeek = document.getElementById('week').value;

    // Calculate the start and end dates of the selected week in the selected month
    var startOfWeek = getStartOfWeek(selectedYear, selectedMonth, selectedWeek);
    var endOfWeek = getEndOfWeek(selectedYear, selectedMonth, selectedWeek);

    // Format the dates
    var startFormatted = formatDate(startOfWeek);
    var endFormatted = formatDate(endOfWeek);

    // Perform any additional calculations or formatting as needed
    var selectedInfo = selectedYear + '年 ' + selectedMonth + '月 第' + selectedWeek + '周（' + startFormatted + ' 至 ' + endFormatted + '）';
    //用于告警功能，将开始结束日期赋值到input
    $('input[name="startdate"]').val(startFormatted);
    $('input[name="enddate"]').val(endFormatted);
    // Display the information on the page
    //document.getElementById('selectedInfo').textContent = selectedInfo;
}

function getStartOfWeek(year, month, week) {
    var firstDayOfMonth = new Date(year, month - 1, 1);
    var startOfWeek = new Date(firstDayOfMonth);
    startOfWeek.setDate(firstDayOfMonth.getDate() + (week - 1) * 7 - startOfWeek.getDay() + 1);
    return startOfWeek;
}

function getEndOfWeek(year, month, week) {
    var startOfWeek = getStartOfWeek(year, month, week);
    var endOfWeek = new Date(startOfWeek);
    endOfWeek.setDate(endOfWeek.getDate() + 6);
    return endOfWeek;
}

function formatDate(date) {
    var year = date.getFullYear();
    var month = (date.getMonth() + 1).toString().padStart(2, '0');
    var day = date.getDate().toString().padStart(2, '0');
    return year + '-' + month + '-' + day;
}