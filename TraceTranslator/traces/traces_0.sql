-- Trace file automatically generated by
-- Event Trace Converter

Use TraceAdaptater;
DROP TABLE IF EXISTS trace;
CREATE TABLE trace (
  `p7` tinytext DEFAULT null,
  `p6` tinytext DEFAULT null,
  `p5` tinytext DEFAULT null,
  `p4` tinytext DEFAULT null,
  `p3` tinytext DEFAULT null,
  `p2` tinytext DEFAULT null,
  `p1` tinytext DEFAULT null,
  `p0` tinytext DEFAULT null,
  `p9` tinytext DEFAULT null,
  `p8` tinytext DEFAULT null,
  msgno int(11) NOT NULL,
  PRIMARY KEY (`msgno`)
) ENGINE=MyISAM AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

INSERT INTO trace SET msgno = 0, `p7` = "0", `p6` = NULL, `p5` = "0", `p4` = "2", `p3` = NULL, `p2` = NULL, `p1` = "0", `p0` = NULL, `p9` = "1", `p8` = "2";
INSERT INTO trace SET msgno = 1, `p7` = "3", `p6` = NULL, `p5` = "4", `p4` = "4", `p3` = "4", `p2` = "2", `p1` = "4", `p0` = "1", `p9` = "1", `p8` = "0";
INSERT INTO trace SET msgno = 2, `p7` = "4", `p6` = NULL, `p5` = "4", `p4` = NULL, `p3` = "0", `p2` = "2", `p1` = NULL, `p0` = "1", `p9` = NULL, `p8` = NULL;
INSERT INTO trace SET msgno = 3, `p7` = NULL, `p6` = NULL, `p5` = "4", `p4` = NULL, `p3` = "2", `p2` = "3", `p1` = "0", `p0` = "1", `p9` = "3", `p8` = NULL;
INSERT INTO trace SET msgno = 4, `p7` = "3", `p6` = NULL, `p5` = NULL, `p4` = NULL, `p3` = "2", `p2` = NULL, `p1` = NULL, `p0` = "1", `p9` = "2", `p8` = "4";
INSERT INTO trace SET msgno = 5, `p7` = "3", `p6` = NULL, `p5` = NULL, `p4` = NULL, `p3` = "1", `p2` = NULL, `p1` = "3", `p0` = NULL, `p9` = "0", `p8` = "3";
INSERT INTO trace SET msgno = 6, `p7` = "2", `p6` = "2", `p5` = NULL, `p4` = "2", `p3` = NULL, `p2` = NULL, `p1` = "2", `p0` = NULL, `p9` = "3", `p8` = NULL;
INSERT INTO trace SET msgno = 7, `p7` = "4", `p6` = "3", `p5` = "3", `p4` = "3", `p3` = NULL, `p2` = "0", `p1` = NULL, `p0` = "2", `p9` = NULL, `p8` = NULL;
INSERT INTO trace SET msgno = 8, `p7` = "0", `p6` = "1", `p5` = NULL, `p4` = NULL, `p3` = "4", `p2` = "0", `p1` = NULL, `p0` = "3", `p9` = NULL, `p8` = NULL;
INSERT INTO trace SET msgno = 9, `p7` = NULL, `p6` = "2", `p5` = "4", `p4` = NULL, `p3` = NULL, `p2` = "3", `p1` = NULL, `p0` = NULL, `p9` = NULL, `p8` = NULL;
INSERT INTO trace SET msgno = 10, `p7` = NULL, `p6` = "0", `p5` = "4", `p4` = NULL, `p3` = NULL, `p2` = "4", `p1` = NULL, `p0` = "4", `p9` = NULL, `p8` = "1";
INSERT INTO trace SET msgno = 11, `p7` = "1", `p6` = "2", `p5` = "4", `p4` = NULL, `p3` = "3", `p2` = "3", `p1` = NULL, `p0` = NULL, `p9` = NULL, `p8` = "0";
INSERT INTO trace SET msgno = 12, `p7` = "0", `p6` = NULL, `p5` = NULL, `p4` = NULL, `p3` = NULL, `p2` = "3", `p1` = "2", `p0` = "3", `p9` = "4", `p8` = NULL;
INSERT INTO trace SET msgno = 13, `p7` = NULL, `p6` = "1", `p5` = NULL, `p4` = "1", `p3` = NULL, `p2` = NULL, `p1` = "1", `p0` = NULL, `p9` = "0", `p8` = NULL;
INSERT INTO trace SET msgno = 14, `p7` = NULL, `p6` = NULL, `p5` = NULL, `p4` = "0", `p3` = "0", `p2` = NULL, `p1` = "0", `p0` = "2", `p9` = "0", `p8` = NULL;
INSERT INTO trace SET msgno = 15, `p7` = "0", `p6` = "3", `p5` = "0", `p4` = NULL, `p3` = "3", `p2` = NULL, `p1` = NULL, `p0` = "2", `p9` = "3", `p8` = NULL;

SELECT DISTINCT traceA0.msgno FROM trace AS traceA0 JOIN ((((SELECT trace4L.* FROM ((SELECT msgno FROM trace AS trace5 WHERE `p0` = "3") AS trace4L INNER JOIN (SELECT msgno FROM trace AS trace5 WHERE "3" = "0") AS trace4R ON trace4L.msgno = trace4R.msgno)) UNION (SELECT trace4L.* FROM ((SELECT msgno FROM trace AS trace5 WHERE `p0` = "2") AS trace4L INNER JOIN (SELECT msgno FROM trace AS trace5 WHERE "2" = "0") AS trace4R ON trace4L.msgno = trace4R.msgno))) UNION (SELECT trace3L.* FROM ((SELECT msgno FROM trace AS trace4 WHERE `p0` = "1") AS trace3L INNER JOIN (SELECT msgno FROM trace AS trace4 WHERE "1" = "0") AS trace3R ON trace3L.msgno = trace3R.msgno))) UNION (SELECT trace2L.* FROM ((SELECT msgno FROM trace AS trace3 WHERE `p0` = "4") AS trace2L INNER JOIN (SELECT msgno FROM trace AS trace3 WHERE "4" = "0") AS trace2R ON trace2L.msgno = trace2R.msgno))) AS traceB0 WHERE traceA0.msgno <= traceB0.msgno
