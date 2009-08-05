@echo off
REM Copyright(c) 2009 Red Hat Middleware, LLC,
REM and individual contributors as indicated by the @authors tag.
REM See the copyright.txt in the distribution for a
REM full listing of individual contributors.
REM
REM This library is free software; you can redistribute it and/or
REM modify it under the terms of the GNU Lesser General Public
REM License as published by the Free Software Foundation; either
REM version 2 of the License, or (at your option) any later version.
REM
REM This library is distributed in the hope that it will be useful,
REM but WITHOUT ANY WARRANTY; without even the implied warranty of
REM MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
REM Lesser General Public License for more details.
REM
REM You should have received a copy of the GNU Lesser General Public
REM License along with this library in the file COPYING.LIB;
REM if not, write to the Free Software Foundation, Inc.,
REM 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA
REM
REM @author Jean-Frederic Clere
REM
@echo on

@if "%1" == "stop" goto stop

"@BASELOC@\httpd-2.2\bin\httpd.exe" -k install
"@BASELOC@\httpd-2.2\bin\httpd.exe" -k start
goto end

:stop
"@BASELOC@\httpd-2.2\bin\httpd.exe" -k stop
"@BASELOC@\httpd-2.2\bin\httpd.exe" -k uninstall
:end
