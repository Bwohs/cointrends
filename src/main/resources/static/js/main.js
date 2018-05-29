var DatatimepickerDefaultOptions = {
    format: 'YYYY-MM-DD HH:mm:ss',
    useCurrent: false,
    todayHighlight: true,
    keepInvalid: true,
}

function isValidDatetimeFormat(datetime){

    var regExpDatetime = /^20[0-9]{2}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) ([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$/;

    if(regExpDatetime.test(datetime)) return true;
    return false;
}

function isDatetimeAfter(datetime){
    if(Date.parse(datetime) >= Date.parse(minDateTime)) return true;
    return false;

}

function isDatetimeBefore(datetime){
    if(Date.parse(datetime) <= Date.parse(maxDateTime)) return true;
    return false;
}

function isStartBeforeEnd(start, end) {
    if(Date.parse(start) < Date.parse(end)) return true;
    return false;
}

function ReplaceSpaceInDateTime(datetime){
    return datetime.replace(/ /, 'T');
}

$(function () {

    $('#datetimepicker1').datetimepicker(DatatimepickerDefaultOptions);
    $('#datetimepicker2').datetimepicker(DatatimepickerDefaultOptions);

    $('#datetimepicker1').datetimepicker('minDate', minDateTime);
    $('#datetimepicker1').datetimepicker('maxDate', maxDateTime);
    $('#datetimepicker2').datetimepicker('minDate', minDateTime);
    $('#datetimepicker2').datetimepicker('maxDate', maxDateTime);

    $("#datetimepicker1").on("change.datetimepicker", function (e) {
        $('#datetimepicker2').datetimepicker('minDate', e.date);   
    });
    $("#datetimepicker2").on("change.datetimepicker", function (e) {
        $('#datetimepicker1').datetimepicker('maxDate', e.date); 
    });

    $("#datetimepicker1input").on("blur", function (e) {
        $('#datetimepicker1').removeClass('datetime-error');
    });

    $("#datetimepicker2input").on("blur", function (e) {
        $('#datetimepicker2').removeClass('datetime-error');
    });

    $("div[id^=datetimepicker] > div.input-group-append").on("click", function (e) {
        $(this).siblings('input[id^=datetimepicker]').val('');
    });


    $('#updateButton').on("click",function(){

        var datetimeStart = $('#datetimepicker1 input').val();                    
        var datetimeEnd = $('#datetimepicker2 input').val();

        $('#datetimepicker1Errors').hide();
        $('#datetimepicker2Errors').hide();

        $('#datetimepicker1Errors > ul').empty();
        $('#datetimepicker2Errors > ul').empty();

        var datetimeStartValidatedFormat = isValidDatetimeFormat(datetimeStart);
        var datetimeEndValidatedFormat = isValidDatetimeFormat(datetimeEnd);

        if(!(datetimeStartValidatedFormat && datetimeEndValidatedFormat)){
            if(!datetimeStartValidatedFormat){
                $('#datetimepicker1Errors > ul').append('<li>Date time must be in format "YYYY-MM-DD HH:mm:ss"</li>');
                $('#datetimepicker1').addClass('datetime-error');
                $('#datetimepicker1Errors').show();
            }

            if(!datetimeEndValidatedFormat){
                $('#datetimepicker2Errors > ul').append('<li>Date time must be in format "YYYY-MM-DD HH:mm:ss"</li>');
                $('#datetimepicker2').addClass('datetime-error');
                $('#datetimepicker2Errors').show();
            }
            return;
        }

        var datetimeStartValidatedValue = isDatetimeAfter(datetimeStart);
        var datetimeEndValidatedValue = isDatetimeBefore(datetimeEnd);

        if(!(datetimeStartValidatedValue && datetimeEndValidatedValue)){
            if(!datetimeStartValidatedValue){
                $('#datetimepicker1Errors > ul').append('<li>Start date time must be after ' + minDateTime + '</li>');
                $('#datetimepicker1').addClass('datetime-error');
                $('#datetimepicker1Errors').show();
            }
            if(!datetimeEndValidatedValue){
                $('#datetimepicker2Errors > ul').append('<li>End date time must be before ' + maxDateTime + '</li>');
                $('#datetimepicker2').addClass('datetime-error');
                $('#datetimepicker2Errors').show();
            }  
            return;
        }

        var datetimeStartBeforeEndValidated = isStartBeforeEnd(datetimeStart,datetimeEnd);

        if(!datetimeStartBeforeEndValidated){
            $('#datetimepicker1Errors > ul').append('<li>Start date time must be before end date time</li>');
            $('#datetimepicker1').addClass('datetime-error');
            $('#datetimepicker1Errors').show();
            return;
        }

        console.log("Validation ok");
        var datetimeStartReplacedSpace = ReplaceSpaceInDateTime(datetimeStart);
        var datetimeEndReplacedSpace = ReplaceSpaceInDateTime(datetimeEnd);

        var apiUrl = "http://localhost:8080/kainos/api/historical/start/" + datetimeStartReplacedSpace + "/end/" + datetimeEndReplacedSpace;
        console.log(apiUrl);
        //                    $.ajax({
        //                        url : apiUrl,
        //                        success : function(data) {
        //
        //
        //                        }
        //
        //                    });

    });

});


//*****************************************************************************
//                                        Charts
//*****************************************************************************
var cryptocurrenciesChartOpts = {
    type: 'line',
    data: {
        labels: [],
        datasets: [{ 
            data: [], //here data for first data-set separated by comma
            label: "BTCUSD",
            borderColor: "#F8A034",
            backgroundColor: "#F8A034",
            fill: false,
            pointRadius: 0,
            pointHitRadius: 10,
            pointHoverRadius: 4,
            borderWidth: 1,
        },{ 
            data: [], //here data for first data-set separated by comma
            label: "ETHUSD",
            borderColor: "#2F3030",
            backgroundColor: "#2F3030",
            fill: false,
            pointRadius: 0,
            pointHitRadius: 10,
            pointHoverRadius: 4,
            borderWidth: 1,
        },{ 
            data: [], //here data for first data-set separated by comma
            label: "LTCUSD",
            borderColor: "#838383",
            backgroundColor: "#838383",
            fill: false,
            pointRadius: 0,
            pointHitRadius: 10,
            pointHoverRadius: 4,
            borderWidth: 1,
        }]
    },
    options: {
        responsive:true,
        maintainAspectRatio: false,
        legend: {
            display: true,
            labels: {
                fontSize: 15,
            }
        },
        title: {
            display: true,
            fontSize: 25,
            text: 'Cryptocurrencies' //title of chart
        },
        scales: {
            xAxes: [{
                display: true,
                type: 'time',
                distribution: 'linear',
                time: {
                    displayFormats: {
                        millisecond: 'HH:mm:ss',
                        second: 'HH:mm:ss',
                        minute: 'HH:mm',
                        hour: 'DD HH:mm',
                        day: 'MM-DD HH',
                        month: 'YYYY-MM-DD',
                    },
                    //                  format: 'YYYY/MM/DD HH:mm:ss',  
                },
                scaleLabel: {
                    display: true,
                    fontSize: 20,
                    labelString: 'Time' //label for x-axis
                }
            }],
            yAxes: [{
                display: true,
                type: 'logarithmic',
//                ticks: {
//                    suggestedMin: 0,    //min value of y-axis
//                    suggestedMax: 40    //max value of y-axis
//                },
                scaleLabel: {
                    display: true,
                    fontSize: 20,
                    labelString: 'Value' //label for y-axis
                }
            }]
        },
        zoom: {
            enabled: true,
            drag: true,
            mode: 'x',
            limits: {
                max: 10,
                min: 0.5
            }
        },
        tooltips: {
            enabled: true,
            mode: 'index',
            position: 'nearest',
            intersect: false,
            caretPadding: 40,
        },
        elements: {
            line: {
                tension: 0, // disables bezier curves
            }
        },
        animation: {
            duration: 0, // general animation time
        },
        hover: {
            animationDuration: 0, // duration of animations when hovering an item
        },
        responsiveAnimationDuration: 0, // animation duration after a resize
    }
};

var cryptocurrenciesChart;

$(function () {
    cryptocurrenciesChart = new Chart($('#cryptocurrenciesChart'), cryptocurrenciesChartOpts);
});


$("#resetButton").on("click", function () {
    cryptocurrenciesChart.resetZoom();
});
