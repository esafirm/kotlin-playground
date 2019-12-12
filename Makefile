.PHONY: coverResult testResult

coverResult: # open coverage result
	open build/reports/jacoco/test/html/index.html

testResult: # open test result
	open build/reports/tests/test/index.html