var DatatimepickerDefaultOptions = {
    format: 'YYYY-MM-DD HH:mm:ss',
    useCurrent: false,
    todayHighlight: true,
    keepInvalid: true,
};

function isValidDatetimeFormat(datetime) {

    var regExpDatetime = /^20[0-9]{2}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) ([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$/;

    if (regExpDatetime.test(datetime)) return true;
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

        console.log("Frontend validation ok");
        var datetimeStartReplacedSpace = ReplaceSpaceInDateTime(datetimeStart);
        var datetimeEndReplacedSpace = ReplaceSpaceInDateTime(datetimeEnd);

        var apiUrl = "/api/historical/start/" + datetimeStartReplacedSpace + "/end/" + datetimeEndReplacedSpace;

        var overlay = $('#overlay');
        $(overlay).show();
        
        $.ajax({
            url : apiUrl,
            success : function(data) {
                
            clearChartData(cryptocurrenciesChart);
                
            console.log(data);
                
                var currenciesData = data.CRYPTOCURRENCIES;

                currenciesData = JSON.parse(JSON.stringify(currenciesData).split('"time":').join('"x":'));
                currenciesData = JSON.parse(JSON.stringify(currenciesData).split('"average":').join('"y":'))

                for (var currency in currenciesData) {
                    if( currenciesData.hasOwnProperty(currency) ) {
                        for(var i = 0; i < cryptocurrenciesChart.data.datasets.length; i++){
                            if(cryptocurrenciesChart.data.datasets[i].label == currency){
                                cryptocurrenciesChart.data.datasets[i].data = currenciesData[currency];
                                break;
                            }
                        }
                    } 
                }
                
  
                var trendsData = data.TRENDS;

                for (var trend in trendsData) {
                    if( trendsData.hasOwnProperty(trend) ) {

                        var trendName = trend + " Trend";

                        var points = [];      
                        var colors = [''];
                        
                        for(var j = 0; j < trendsData[trend].length; j++){
                            var color = '';
                            
                            points.push({
                                x: trendsData[trend][j].t2,
                                y: trendsData[trend][j].value2,
                            });
                            
                            points.push({
                                x: trendsData[trend][j].t1,
                                y: trendsData[trend][j].value1,
                            });

                            if (trendsData[trend][j].slope == 'falling') color = 'red';
                            else if (trendsData[trend][j].slope == 'rising') color = 'blue';
                            else color = 'yellow';
                            
                            colors.push(color);
                            colors.push('transparent');
                        }

                        for(var i = 0; i < cryptocurrenciesChart.data.datasets.length; i++){
                            if(cryptocurrenciesChart.data.datasets[i].label == trendName){
                                cryptocurrenciesChart.data.datasets[i].data = points;
                                cryptocurrenciesChart.data.datasets[i].colors = colors;
                                break;
                            }
                        }
                    } 
                }
                
                
                var dependenciesData = data.DEPENDENCIES;
                
                var selectedCryptocurrency = 'BTCUSD';
                var selectedDependencies = dependenciesData[selectedCryptocurrency];
                
                $( "#trend-boxes" ).empty();
                
                for(var i = 0; i < selectedDependencies.length; i++){
                    var slope = selectedDependencies[i].slope;
                    var date = selectedDependencies[i].time;
                    var effects = selectedDependencies[i].effects;
   
                    var trendBoxCode = generateTrendBoxCode(selectedCryptocurrency,slope,date,effects);

                    $( "#trend-boxes" ).append(trendBoxCode);
                    
                }
                
                cryptocurrenciesChart.update();
                
                $(overlay).hide();
            }

        });

    });

});


//*****************************************************************************
//                                        Charts
//*****************************************************************************
Chart.defaults.multicolorLine = Chart.defaults.line;
Chart.controllers.multicolorLine = Chart.controllers.line.extend({
    draw: function(ease) {
        var
        startIndex = 0,
            meta = this.getMeta(),
            points = meta.data || [],
            colors = this.getDataset().colors,
            area = this.chart.chartArea,
            originalDatasets = meta.dataset._children
        .filter(function(data) {
            return !isNaN(data._view.y);
        });

        function _setColor(newColor, meta) {
            meta.dataset._view.borderColor = newColor;
        }

        if (!colors) {
            Chart.controllers.line.prototype.draw.call(this, ease);
            return;
        }

        for (var i = 2; i <= colors.length; i++) {
            if (colors[i-1] !== colors[i]) {
                _setColor(colors[i-1], meta);
                meta.dataset._children = originalDatasets.slice(startIndex, i);
                meta.dataset.draw();
                startIndex = i - 1;
            }
        }

        meta.dataset._children = originalDatasets.slice(startIndex);
        meta.dataset.draw();
        meta.dataset._children = originalDatasets;

        points.forEach(function(point) {
            point.draw(area);
        });
    }
});

var cryptocurrenciesChartOpts = {
    type: 'multicolorLine',
    data: {
        labels: [],
        datasets: [{ 
            data: [{}], //here data for first data-set separated by comma
            label: "BTCUSD",
            borderColor: "#F8A034",
            backgroundColor: "#F8A034",
            fill: false,
            pointRadius: 0,
            pointHitRadius: 5,
            pointHoverRadius: 3,
            borderWidth: 1,
        },{ 
            data: [{}], //here data for first data-set separated by comma
            label: "ETHUSD",
            borderColor: "#e0e011",
            backgroundColor: "#e0e011",
            fill: false,
            pointRadius: 0,
            pointHitRadius: 5,
            pointHoverRadius: 3,
            borderWidth: 1,
        },{ 
            data: [{}], //here data for first data-set separated by comma
            label: "LTCUSD",
            borderColor: "#eb11e3",
            backgroundColor: "#eb11e3",
            fill: false,
            pointRadius: 0,
            pointHitRadius: 5,
            pointHoverRadius: 3,
            borderWidth: 1,
        },{ 
            data: [{}], //here data for first data-set separated by comma
            label: "BTCUSD Trend",
            borderColor: "#a25b04",
            backgroundColor: "#a25b04",
            fill: false,
            pointRadius: 0,
            pointHitRadius: 5,
            pointHoverRadius: 3,
            borderWidth: 3,
            colors: [''],
        },{ 
            data: [{}], //here data for first data-set separated by comma
            label: "ETHUSD Trend",
            borderColor: "#83830b",
            backgroundColor: "#83830b",
            fill: false,
            pointRadius: 0,
            pointHitRadius: 5,
            pointHoverRadius: 3,
            borderWidth: 3,
            colors: [''],
        },{ 
            data: [{}], //here data for first data-set separated by comma
            label: "LTCUSD Trend",
            borderColor: "#71096d",
            backgroundColor: "#71096d",
            fill: false,
            pointRadius: 0,
            pointHitRadius: 5,
            pointHoverRadius: 3,
            borderWidth: 3,
            colors: [''],
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
                position: 'right',
                ticks: {
//                    suggestedMin: 0,    //min value of y-axis
//                    suggestedMax: 40,    //max value of y-axis
                        callback: function(...args) {
                        const value = Chart.Ticks.formatters.logarithmic.call(this, ...args);
                        if (value.length) {
                            return Number(value).toLocaleString()
                        }
                        return value;
                    }
                },
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
            mode: 'nearest',
            position: 'nearest',
            intersect: false,
            caretPadding: 5,
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
    $('[data-toggle="tooltip"]').tooltip({
        trigger : 'hover'
    });
    cryptocurrenciesChart = new Chart($('#cryptocurrenciesChart'), cryptocurrenciesChartOpts);
    cryptocurrenciesChart.update();
});


$("#resetButton").on("click", function () {
    cryptocurrenciesChart.resetZoom();
});

function clearChartData(chart){
    
    chart.data.labels.length = 0;
    chart.data.datasets.forEach(function(dataset){
        dataset.data.length = 0;
        if(dataset.colors){
            dataset.colors.length = 0;
        }
    });
}


function generateTrendBoxCode(currencyName,slope,date,effects){
    var code = ' <div class="trend-box"> \
                    <div class="arrow-container">';
    
    if(slope == 'rising') code += '<img src="img/arrowupgreen.svg" alt="Green up arrow"/>';
    else if(slope == 'falling') code += '<img src="img/arrowdownred.svg" alt="Red down arrow"/>';
         
    code += '</div> \
            <div class="arrow-desription"> \
            <p class="title">' + currencyName + ' ';
    
    if(slope == 'rising') code += 'Growth';
    else if(slope == 'falling') code += 'Drop';
    
    code += ' - ' + date + '</p> \
            <p class="description">';

    
    var growing = "";
    var dropping = "";
    var constant = "";
    
    for (var effect in effects) {
        if (effects.hasOwnProperty(effect)) {
            if(effects[effect] == 'rising'){
                if(growing.length != 0) growing += " and ";
                growing += effect;
            }else if(effects[effect] == 'falling'){
                if(dropping.length != 0) dropping += " and ";
                dropping += effect;
            }else {
                if(constant.length != 0) constant += " and ";
                constant += effect;
            }
        }
    }
    
    if(growing.length != 0) code += growing + ' started to grow';
    if(dropping.length != 0) {
        if(growing.length != 0) code += " and ";
        code += dropping + ' started to drop';
    }
    if(constant.length != 0) {
        if(growing.length != 0 || dropping.length != 0) code += " and ";
        code += constant + ' was stable';
    }
    
    code += ' just after BTCUSD</p> \
                    </div> \
                </div>';
    return code;
}

