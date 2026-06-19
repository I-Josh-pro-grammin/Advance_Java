$base = "http://localhost:8080"
$results = @()

function Test-Api {
    param($Name, $Method, $Url, $Body, $ExpectedStatus, $Validator)
    $entry = [ordered]@{ Name=$Name; Method=$Method; Url=$Url; Expected=$ExpectedStatus; Actual=$null; Pass=$false; Response=$null; Error=$null }
    try {
        $params = @{ Uri=$Url; Method=$Method; ContentType="application/json"; ErrorAction="Stop" }
        if ($Body) { $params.Body = ($Body | ConvertTo-Json -Depth 5 -Compress) }
        $resp = Invoke-WebRequest @params -UseBasicParsing
        $entry.Actual = $resp.StatusCode
        try { $entry.Response = $resp.Content | ConvertFrom-Json } catch { $entry.Response = $resp.Content }
    } catch {
        $entry.Actual = $_.Exception.Response.StatusCode.value__
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $entry.Response = $reader.ReadToEnd() | ConvertFrom-Json
        $entry.Error = $_.Exception.Message
    }
    $statusOk = ($entry.Actual -eq $ExpectedStatus)
    $customOk = $true
    if ($Validator -and $entry.Response) { $customOk = & $Validator $entry.Response }
    $entry.Pass = $statusOk -and $customOk
    $script:results += [pscustomobject]$entry
    return $entry
}

Write-Host "=== QA API TEST RUN ===" -ForegroundColor Cyan

# Health - list employees
Test-Api "GET employees" GET "$base/api/employees" $null 200 {} | Out-Null

# Employee valid
$validEmp = @{
  firstName="Test"; lastName="Active"; email="test.active.qa@gov.rw"; district="Gasabo"; mobile="+250788999001"
  dateOfBirth="1995-01-15"; employeeId="EMP-QA-001"; department="Finance"; position="Analyst"
  salary=700000; status="ACTIVE"; joiningDate="2024-01-01"
}
Test-Api "POST employee valid" POST "$base/api/employees" $validEmp 201 { param($r) $r.employeeId -eq "EMP-QA-001" } | Out-Null

# Duplicate employeeId
$dupId = $validEmp.Clone(); $dupId.email = "other.qa@gov.rw"
Test-Api "POST duplicate employeeId" POST "$base/api/employees" $dupId 409 {} | Out-Null

# Duplicate email
$dupEmail = $validEmp.Clone(); $dupEmail.employeeId = "EMP-QA-999"
Test-Api "POST duplicate email" POST "$base/api/employees" $dupEmail 409 {} | Out-Null

# Invalid email
$badEmail = $validEmp.Clone(); $badEmail.email = "not-an-email"; $badEmail.employeeId = "EMP-QA-002"
Test-Api "POST invalid email" POST "$base/api/employees" $badEmail 400 {} | Out-Null

# Null required fields
Test-Api "POST missing fields" POST "$base/api/employees" @{ firstName="Only" } 400 {} | Out-Null

# Negative salary
$negSal = $validEmp.Clone(); $negSal.email = "neg.qa@gov.rw"; $negSal.employeeId = "EMP-QA-003"; $negSal.salary = -100
Test-Api "POST negative salary" POST "$base/api/employees" $negSal 400 {} | Out-Null

# INACTIVE employee
$inactiveEmp = @{
  firstName="Test"; lastName="Inactive"; email="test.inactive.qa@gov.rw"; district="Kicukiro"; mobile="+250788999002"
  dateOfBirth="1990-06-01"; employeeId="EMP-QA-INACTIVE"; department="IT"; position="Dev"
  salary=500000; status="INACTIVE"; joiningDate="2023-06-01"
}
Test-Api "POST inactive employee" POST "$base/api/employees" $inactiveEmp 201 {} | Out-Null

# Deduction create
$ded = @{ name="QA_TestDed"; percentage=10; category="DEDUCTION" }
Test-Api "POST deduction valid" POST "$base/api/deductions" $ded 201 {} | Out-Null

# Duplicate deduction
Test-Api "POST duplicate deduction" POST "$base/api/deductions" $ded 409 {} | Out-Null

# Invalid percentage
Test-Api "POST invalid deduction %" POST "$base/api/deductions" @{ name="BadPct"; percentage=150; category="DEDUCTION" } 400 {} | Out-Null

# Negative percentage  
Test-Api "POST negative deduction %" POST "$base/api/deductions" @{ name="NegPct"; percentage=-5; category="DEDUCTION" } 400 {} | Out-Null

# Payroll generate June 2026 (may fail if already exists - try July 2026)
$payroll = @{ month=7; year=2026 }
$gen = Test-Api "POST payroll generate" POST "$base/api/payroll/generate" $payroll 201 {} 

# Duplicate payroll
Test-Api "POST duplicate payroll" POST "$base/api/payroll/generate" $payroll 409 {} | Out-Null

# Get payslips
Test-Api "GET payslips period" GET "$base/api/payroll/payslips?month=7&year=2026" $null 200 {} | Out-Null

# Get employee payslip - find Peter (id 1 typically)
Test-Api "GET employee payslip" GET "$base/api/payroll/payslips/employee/1/7/2026" $null 200 {} | Out-Null

# Messages
Test-Api "GET messages period" GET "$base/api/payroll/messages?month=7&year=2026" $null 200 {} | Out-Null
Test-Api "GET messages employee" GET "$base/api/payroll/messages/employee/1" $null 200 {} | Out-Null
Test-Api "GET messages invalid employee" GET "$base/api/payroll/messages/employee/99999" $null 404 {} | Out-Null

# Approve payroll
$approve = Test-Api "POST payroll approve" POST "$base/api/payroll/approve" $payroll 200 {}

# Not found
Test-Api "GET employee not found" GET "$base/api/employees/99999" $null 404 {} | Out-Null

# Malformed JSON
try {
  Invoke-WebRequest -Uri "$base/api/employees" -Method POST -Body "{bad json" -ContentType "application/json" -UseBasicParsing
} catch {
  $results += [pscustomobject]@{ Name="POST malformed JSON"; Method="POST"; Url="$base/api/employees"; Expected=400; Actual=$_.Exception.Response.StatusCode.value__; Pass=($_.Exception.Response.StatusCode.value__ -eq 400); Response=$null; Error=$null }
}

# User duplicate
Test-Api "POST duplicate user employee" POST "$base/api/users" @{ username="dupuser"; password="x"; role="EMPLOYEE"; employeeId=1 } 409 {} | Out-Null

$results | Format-Table Name, Method, Expected, Actual, Pass -AutoSize
Write-Host "`nPeter payslip from generate:" -ForegroundColor Yellow
$gen.Response | Where-Object { $_.empId -eq "EMP-001" } | ConvertTo-Json -Depth 5
Write-Host "`nApprove response sample:" -ForegroundColor Yellow
$approve.Response | Select-Object -First 1 | ConvertTo-Json -Depth 5

# Export for report
$results | ConvertTo-Json -Depth 6 | Out-File "qa-results.json" -Encoding utf8
