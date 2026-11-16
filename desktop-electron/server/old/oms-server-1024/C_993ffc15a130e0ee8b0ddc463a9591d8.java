import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_993ffc15a130e0ee8b0ddc463a9591d8
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String sResult=mapRow.get("MSGINFO").toString();
sResult=String.format("<span onclick=\"layer.alert('"+sResult+"')\">查看</span>",sResult);
return sResult;
    }
}
