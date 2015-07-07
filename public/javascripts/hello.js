if (window.console) {
  console.log("Welcome to your Play application's JavaScript!");
}


function assignShift() {
    //alert("hello world");
    var name = document.getElementById("inputName0").value;
    var date = toDate(document.getElementById("inputDate0").value);
    /*var http = new XMLHttpRequest();
    var url = "/shift";
    http.open("POST", url, true);

    http.send("date="+date+"&name="+name);*/

    var http = new XMLHttpRequest();
    var url = "/shift";
    var params = "name="+name+"&date="+date;
    http.open("POST", url, true);

    //Send the proper header information along with the request
    http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    http.setRequestHeader("Content-length", params.length);
    http.setRequestHeader("Connection", "close");

    http.onreadystatechange = function() {//Call a function when the state changes.
        if(http.readyState == 4 && http.status == 200) {
            var tr = document.getElementById(date)
            if (tr) {
                var c2 = document.createElement('td')
                c2.innerHTML = name.toLowerCase()
                tr.appendChild(c2)
            }
        }
    }

    http.send(params);
}

function assign(shiftDate, name) {
    var date = toDate(shiftDate);
    var http = new XMLHttpRequest();
    var url = "/shift";
    var params = "name="+name+"&date="+date;
    http.open("POST", url, true);

    //Send the proper header information along with the request
    http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    http.setRequestHeader("Content-length", params.length);
    http.setRequestHeader("Connection", "close");

    http.onreadystatechange = function() {//Call a function when the state changes.
        if(http.readyState == 4 && http.status == 200) {
            var tr = document.getElementById(date)
            if (tr) {
                deleteAllNodes(tr)
                var c1 = document.createElement('td')
                c1.innerHTML = date
                tr.appendChild(c1)
                var c2 = document.createElement('td')
                c2.innerHTML = name.toLowerCase()
                tr.appendChild(c2)
            }
        }
    }

    http.send(params);
}

function deleteAllNodes(myNode) {
    while (myNode.firstChild) {
        myNode.removeChild(myNode.firstChild);
    }
}

function unassignShift() {
    //alert("hello world");
    var name = document.getElementById("inputName").value;
    var date = toDate(document.getElementById("inputDate").value);
    var http = new XMLHttpRequest();
    var url = "/shift?date="+date+"&name="+name;
    http.open("DELETE", url, true);

    http.onreadystatechange = function() {//Call a function when the state changes.
        if(http.readyState == 4 && http.status == 200) {
            var tr = document.getElementById(date)
            if (tr) {
                deleteAllNodes(tr)
                var c2 = document.createElement('td')
                c2.innerHTML = date
                tr.appendChild(c2)
            }
        }
    }

    http.send();
}

function makeUL(array) {
    // Create the list element:
    var list = document.createElement('ul');

    for(var i = 0; i < array.length; i++) {
        // Create the list item:
        var item = document.createElement('li');

        // Set its contents:
        item.appendChild(document.createTextNode(JSON.stringify(array[i])));

        // Add it to the list:
        list.appendChild(item);
    }

    // Finally, return the constructed list:
    return list;
}

function makeTable(schedule) {
    schedule.sort(function(a, b) {
        return new Date(a.d) - new Date(b.d);
    });

    var table = document.createElement('table')

    for (var i = 0; i < schedule.length; i++) {
        var shift = schedule[i];
        var user = shift['username'];

        var r = document.createElement('tr')
        var c1 = document.createElement('td')
        var c2 = document.createElement('td')

        var d = getNumbers(shift['d'])//new Date(shift['d'])
        var txt = d[0] + "-" + d[1] + "-" + d[2]

        r.id = txt

        //c1.innerHTML = d.getFullYear() + "-" + (d.getMonth() + 1) + "-" + d.getUTCDate()
        c1.innerHTML = txt
        c2.innerHTML = user['name']

        table.appendChild(r)
        r.appendChild(c1)
        r.appendChild(c2)
    }

    return table
}

function isWeekend(year, month, d) {
    var date = new Date(year, month, d)
    var day = date.getDay()
    return day == 0 || day == 6
}

function getHoliday(month, day) {
    var holiday = {"January 1": "New Year's Day",
    "1 19": "Martin Luther King Jr's Birthday",
    "2 16": "President's Day",
    "3 31": "Cesar Chavez Day",
    "5 25": "Memorial Day",
    "7 4": "Independence Day",
    "9 7": "Labor Day",
    "11 11": "Veteran's Day",
    "11 26": "Thanksgiving Day",
    "11 27": "Day after Thanksgiving Day",
    "12 25": "Christmas Day"};

    return holiday[month + " " + day]
}

function isBelowDate(ms, year, month, day) {
    var date = getNumbers(ms)
    var y = date[0]
    var m = date[1]
    var d = date[2]

    return y < year || (y == year && m < month) || (y == year && m == month && d < day)

}

function isEqualDate(ms, year, month, day) {
    var date = getNumbers(ms)
    var y = date[0]
    var m = date[1]
    var d = date[2]

    return y == year && m == month && d == day

}

function appendAssignButton(tr, date) {
    var button = document.createElement('button')
    button.innerHTML = 'assign'
    button.onclick = function () {
        var name = prompt("Please enter name", "");
        if (name) {
            assign(date, name)
        }
    }
    tr.appendChild(button)
}

function makeTable2(schedule, startDay, endDay, month, year) {
    schedule.sort(function(a, b) {
        var t1 =  new Date(a.d)
        var t2 = new Date(b.d)
        return t1 - t2;
    });

    var table = document.createElement('table')
    var i = 0

    for (var d = startDay; d <= endDay; d+=1) {
        var r = document.createElement('tr')
        var c1 = document.createElement('td')
        var txt = year + "-" + (month+1) + "-" + d

        r.id = txt
        c1.appendChild(document.createTextNode(txt))

        table.appendChild(r)
        r.appendChild(c1)

        while (i < schedule.length && isBelowDate(schedule[i].d, year, month+1, d))
            i += 1

        if (i < schedule.length && isEqualDate(schedule[i].d, year, month+1, d)) {
            var shift = schedule[i]
            var user = shift['username']
            var c2 = document.createElement('td')

            c2.appendChild(document.createTextNode(user['name']))
            r.appendChild(c2)

            i += 1
        } else if (getHoliday(month+1, d) != undefined) {
            var c2 = document.createElement('td')
            c2.appendChild(document.createTextNode(getHoliday(month+1, d)))
            r.appendChild(c2)
        } else if (isWeekend(year, month, d)) {
            var c2 = document.createElement('td')
            c2.appendChild(document.createTextNode("weekend"))
            r.appendChild(c2)
        } else {
            appendAssignButton(r, txt)
        }
    }

    return table
}

function getCurrentMonthSchedule() {

    var date = new Date()
    var year = date.getFullYear()
    var month = date.getMonth()

    var start = new Date(year, month, 1)
    var end = new Date(year, month+1, 0)

    var url = "/schedule?startDate="+start.toISOString()+"&endDate="+end.toISOString()

    var http = new XMLHttpRequest()
    http.open("GET", url, true)

    http.onreadystatechange = function() {
        if (http.readyState == 4 && http.status == 200) {
            //alert(http.responseText)
            var obj = JSON.parse(http.responseText)
            var e = document.getElementById('output')
            //e.innerHTML = ''
            deleteAllNodes(e)
            e.appendChild(makeTable2(obj, 1, end.getDate(), month, year))
        }
    }

    http.send()
}

function getUserSchedule() {

    var start = toDate(document.getElementById("inputStartDate").value)
    var end = toDate(document.getElementById("inputEndDate").value)
    var name = document.getElementById("inputName2").value

    var url = "/schedule/name?name="+name+"&startDate="+start+"&endDate="+end

    var http = new XMLHttpRequest()
    http.open("GET", url, true)

    http.onreadystatechange = function() {
        if (http.readyState == 4 && http.status == 200) {
            //alert(http.responseText)
            var obj = JSON.parse(http.responseText)
            var e = document.getElementById('output')
            //e.innerHTML = ''
            deleteAllNodes(e)
            e.appendChild(makeTable(obj))
        }
    }

    http.send()
}

function swapShifts() {
    var date1 = toDate(document.getElementById("inputDate1").value);
    var date2 = toDate(document.getElementById("inputDate2").value);
    var http = new XMLHttpRequest();
    var url = "/shift/swap?date1="+date1+"&date2="+date2;
    http.open("PUT", url, true);

    http.onreadystatechange = function() {//Call a function when the state changes.
        if(http.readyState == 4 && http.status == 200) {
            var tr1 = document.getElementById(date1)
            var tr2 = document.getElementById(date2)
            var obj = JSON.parse(http.responseText)

            if (tr1) {
                deleteAllNodes(tr1)

                var d1 = document.createElement('td')
                d1.innerHTML = date1
                tr1.appendChild(d1)

                var n1 = document.createElement('td')
                n1.innerHTML = obj.name1
                tr1.appendChild(n1)
            }

            if (tr2) {
                deleteAllNodes(tr2)

                var d2 = document.createElement('td')
                d2.innerHTML = date2
                tr2.appendChild(d2)

                var n2 = document.createElement('td')
                n2.innerHTML = obj.name2
                tr2.appendChild(n2)
            }
        }
    }

    http.send();
}

function isMonth(d) {
    return d >= 1 && d <= 12
}

function isDay(d) {
    return d >= 1 && d <= 31
}

function getNumbers(str) {
    return str.match(/^\d+|\d+\b|\d+(?=\w)/g)
             .map(function (v) {return +v;});
}

function toDate(str) {
    var d = getNumbers(str)
    var result = ""
    var today = new Date()

    switch (d.length) {
        case 1:
        if (isDay(d[0])) {
            result = today.getFullYear() + "-" + (today.getMonth()+1) + "-" + d[0]  // dd
        }
        break;
        case 2:
        if (isDay(d[1])) {
            result = today.getFullYear() + "-" + d[0] + "-" + d[1]  // mm/dd
        } else {
            result = d[1] + "-" + d[0] + "-" + 1 // mm/yyyy
        }
        break;
        case 3:
        if (isMonth(d[0])) {
            result = d[2] + "-" + d[0] + "-" + d[1] // mm/dd/year
        } else {
            result = d[0] + "-" + d[1] + "-" + d[2] // year/mm/dd
        }
        break;
    }

    return result;
}