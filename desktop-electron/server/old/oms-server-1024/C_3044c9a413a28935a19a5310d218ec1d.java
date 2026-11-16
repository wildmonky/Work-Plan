import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_3044c9a413a28935a19a5310d218ec1d
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return "<span datavalue='"+mapRow.get("NOTICE_CONTENT")+"' onclick='"+jsFunctionName+"();'>查看</span>";

    }
}
