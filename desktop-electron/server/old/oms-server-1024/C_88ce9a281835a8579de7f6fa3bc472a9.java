import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_88ce9a281835a8579de7f6fa3bc472a9
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String sResult=mapRow.get("FLAG_NOTIFY_ELINK").toString();
sResult="1".equals(sResult)?"已发文字通知":("0".equals(sResult)?"未发文字通知":sResult);
return sResult;
    }
}
