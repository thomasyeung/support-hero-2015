if (window.console) {
  console.log("Welcome to your Play application's JavaScript!");
}


function assignShift() {
    //alert("hello world");
    var name = document.getElementById("inputName0").value;
    var date = toDate(document.getElementById("inputDate0").value);
    var num = getNumbers(date)

    if (isWorkday(num[0], num[1], num[2]))
        assign(date, name)
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

                appendDate(tr, date)
                appendUserAndAction(tr, date, name.toLowerCase())
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
    var name = document.getElementById("inputName").value;
    var date = toDate(document.getElementById("inputDate").value);
    var num = getNumbers(date)

    if (isWorkday(num[0], num[1], num[2])) {
        unassign(date, name)
    }
}

function unassign(shiftDate, name) {
    var date = toDate(shiftDate)
    var http = new XMLHttpRequest();
    var url = "/shift?date="+date+"&name="+name;
    http.open("DELETE", url, true);

    http.onreadystatechange = function() {//Call a function when the state changes.
        if(http.readyState == 4 && http.status == 200) {
            var tr = document.getElementById(date)
            if (tr) {
                deleteAllNodes(tr)

                appendDate(tr, date)
                appendAssignButton(tr, date)
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

function populateTable(table, schedule) {
    schedule.sort(function(a, b) {
        return new Date(a.d) - new Date(b.d);
    });

    for (var i = 0; i < schedule.length; i++) {
        var shift = schedule[i];
        var user = shift['username'];
        //var r = document.createElement('tr')

        var d = getNumbers(shift['d'])//new Date(shift['d'])
        var txt = d[0] + "-" + d[1] + "-" + d[2]
        var r = createTableRow(i, 0, txt)

        //r.id = txt

        table.appendChild(r)

        appendDate(r, txt)
        appendUserAndAction(r, txt, user['name'])
    }

    return table
}

function isWeekend(year, month, d) {
    var date = new Date(year, month, d)
    var day = date.getDay()
    return day == 0 || day == 6
}

function getHoliday(month, day) {
    var holiday = {"1 1": "New Year's Day",
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

    var str = holiday[month + " " + day]
    return str
}

function isWorkday(year, month, day) {
    return !isWeekend(year, month-1, day) && getHoliday(month, day) == undefined
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
    var c = document.createElement('td')
    c.className = "col-xs-4 text-left"

    var button = document.createElement('button')
    button.className="btn-xs btn-primary"
    button.style="margin:2px;"
    button.innerHTML = 'assign'
    button.onclick = function () {
        var name = prompt("Please enter a name", "");
        if (name) {
            assign(date, name)
        }
    }
    tr.appendChild(c)
    c.appendChild(button)

    appendUndoableButton(tr, date, "")
}

function appendUnassignButton(tr, date, name) {
    var button = document.createElement('button')
    button.innerHTML = 'unassign'
    button.className="btn-xs btn-primary"
    button.style="margin:2px;"
    button.onclick = function () {
        unassign(date, name)
    }
    tr.appendChild(button)
}

function appendSwapButton(tr, date) {
    var button = document.createElement('button')
    button.innerHTML = 'swap'
    button.className="btn-xs btn-primary"
    button.style="margin:2px;"
    button.onclick = function () {
        var date2 = prompt("Date 1 is " + date + "\n\nPlease enter date 2", "");
        if (date2) {
            swapShifts2(date, toDate(date2))
        }
    }
    tr.appendChild(button)
}

// add user
function appendUserAndAction(tr, date, name) {
    var c2 = document.createElement('td')
    c2.className = "col-xs-4 text-left"
    c2.appendChild(document.createTextNode(name))
    tr.appendChild(c2)

    appendUndoableButton(tr, date, name)
    appendSwapButton(tr, date)
    appendUnassignButton(tr, date, name)
}

function appendDate(tr, txt) {
    var c1 = document.createElement('td')
    c1.className = "col-xs-2 text-left"
    c1.appendChild(document.createTextNode(txt))
    tr.appendChild(c1)
}

function createTableRow(day, startDay, id) {
    var ctr = day - startDay
    var tr = document.createElement('tr')
    tr.id = id
    if (ctr % 2 == 0) {
        tr.style = "background-color:Azure;"
    }
    return tr
}

function appendEmptyColumn(tr) {
    var c = document.createElement('td')
    tr.appendChild(c)
}

function appendWeekend(tr) {
    var c2 = document.createElement('td')
    c2.className = "col-xs-4 text-left"
    var span = document.createElement("span")
    span.className = "text-success"
    span.appendChild(document.createTextNode("weekend"))
    c2.appendChild(span)
    tr.appendChild(c2)

    appendEmptyColumn(tr)
}

function populateTable2(table, schedule, startDay, endDay, month, year) {
    schedule.sort(function(a, b) {
        var t1 =  new Date(a.d)
        var t2 = new Date(b.d)
        return t1 - t2;
    });

    var i = 0

    for (var d = startDay; d <= endDay; d+=1) {
        var txt = year + "-" + (month+1) + "-" + d

        var r = createTableRow(d, startDay, txt) //document.createElement('tr')
        //r.id = txt
        table.appendChild(r)

        appendDate(r, txt)

        while (i < schedule.length && isBelowDate(schedule[i].d, year, month+1, d))
            i += 1

        if (i < schedule.length && isEqualDate(schedule[i].d, year, month+1, d)) {
            var shift = schedule[i]
            var user = shift['username']

            appendUserAndAction(r, txt, user['name'])

            i += 1
        } else if (getHoliday(month+1, d) != undefined) {
            var c2 = document.createElement('td')
            c2.className = "col-xs-4 text-left"
            var span = document.createElement('span')
            span.className="text-danger"
            span.appendChild(document.createTextNode(getHoliday(month+1, d)))
            c2.appendChild(span)
            r.appendChild(c2)
            appendEmptyColumn(r)
        } else if (isWeekend(year, month, d)) {
            appendWeekend(r)
        } else {
            appendAssignButton(r, txt)
            appendEmptyColumn(r)
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

            var obj = JSON.parse(http.responseText)
            var e = document.getElementById('output')

            deleteAllNodes(e)
            populateTable2(e, obj, 1, end.getDate(), month, year)
        }
    }

    http.send()
}

function getNextMonthSchedule() {

    var date = new Date()
    var year = date.getFullYear()
    var month = date.getMonth() + 1

    var start = new Date(year, month, 1)
    var end = new Date(year, month+1, 0)

    var url = "/schedule?startDate="+start.toISOString()+"&endDate="+end.toISOString()

    var http = new XMLHttpRequest()
    http.open("GET", url, true)

    http.onreadystatechange = function() {
        if (http.readyState == 4 && http.status == 200) {

            var obj = JSON.parse(http.responseText)
            var e = document.getElementById('output')

            deleteAllNodes(e)
            populateTable2(e, obj, 1, end.getDate(), month, year)
        }
    }

    http.send()
}

function getUserSchedule() {

    var start = document.getElementById("inputStartDate").value ?
        toDate(document.getElementById("inputStartDate").value) : ""
    var end = document.getElementById("inputEndDate").value ?
        toDate(document.getElementById("inputEndDate").value) : ""
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
            populateTable(e, obj)
        }
    }

    http.send()
}

function swapShifts() {
    var date1 = toDate(document.getElementById("inputDate1").value);
    var date2 = toDate(document.getElementById("inputDate2").value);
    var num1 = getNumbers(date1)
    var num2 = getNumbers(date2)
    var w1 = isWorkday(num1[0], num1[1], num1[2])
    var w2 = isWorkday(num2[0], num2[1], num2[2])

    if (w1 && w2)
        swapShifts2(date1, date2)
}

function swapShifts2(date1, date2) {
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
                appendDate(tr1, date1)
                appendUserAndAction(tr1, date1, obj.name1)
            }

            if (tr2) {
                deleteAllNodes(tr2)
                appendDate(tr2, date2)
                appendUserAndAction(tr2, date2, obj.name2)
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
        result = isMonth(d[0]) ? d[2] + "-" + d[0] + "-" + d[1] : d[0] + "-" + d[1] + "-" + d[2]
        break;
    }

    if (!result) {
        alert("Invalid date: " + str + ". Please use one of the following formats: mm/dd/yyyy, mm/dd, dd")
    }

    return result;
}

function getTodaysSupportHero() {

    var date = new Date()
    var y = date.getFullYear();
    var m = date.getMonth() + 1;
    var d = date.getDate();

    var url = "/shift?date=" + y + "-" + m + "-" + d;

    var http = new XMLHttpRequest()
    http.open("GET", url, true)

    http.onreadystatechange = function() {
        if (http.readyState == 4 && http.status == 200) {
            //alert(http.responseText)
            var obj = JSON.parse(http.responseText)
            var e = document.getElementById('heading1')
            if (obj && obj.username && obj.username.name) {
                e.innerHTML = "Today's Support Hero is " + obj.username.name.toUpperCase()
            }
        }
    }

    http.send()
}

/*function getNames(obj) {
    var list = []

    for (var i=0; i < obj.length; i++) {
        var undoable = obj[i]
        var user = undoable.username
        list.push(user.name)
    }

    return list
}*/

function existsInList(list, str) {
    if (!Array.prototype.indexOf) {
       Array.prototype.indexOf = function(item) {
          var i = this.length;
          while (i--) {
             if (this[i] === item) return i;
          }
       }
       return -1;
    }

    return list.indexOf(str) >= 0
}

function createUndoable(date, name) {
    var http = new XMLHttpRequest();
    var url = "/undoable";
    var params = "name="+name+"&date="+date;
    http.open("POST", url, true);

    //Send the proper header information along with the request
    http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    http.setRequestHeader("Content-length", params.length);
    http.setRequestHeader("Connection", "close");
    http.send(params);
}

function deleteUndoable(date, name) {
    //var date = toDate(date)
    var http = new XMLHttpRequest();
    var url = "/undoable?date="+date+"&name="+name;
    http.open("DELETE", url, true);
    http.send();
}

function appendUndoableButton(tr, date, name) {
    var button = document.createElement('button')
    button.innerHTML = 'undoable'
    button.className="btn-xs btn-warning"
    button.style="margin:2px;"
    button.onclick = function () {

        var url = "/undoable?date=" + date;
        var http = new XMLHttpRequest()
        http.open("GET", url, true)

        http.onreadystatechange = function() {
            if (http.readyState == 4 && http.status == 200) {
                var obj = JSON.parse(http.responseText)
                var name2 = prompt("Undoable: " + obj.toString() + "\n\nPlease enter a name", "");

                if (name2 && existsInList(obj, name2)) {
                    deleteUndoable(date, name2)
                } else if (name2 && name != name2) {
                    createUndoable(date, name2)
                }
            }
        }

        http.send()
    }
    tr.appendChild(button)
}