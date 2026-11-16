import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_e8a35d9435f551c7091a4be0c6e64ff1
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String sResult=mapRow.get("FLAG_NOTIFY_TEL").toString();
sResult="1".equals(sResult)?"已电话通知":("0".equals(sResult)?"未电话通知":sResult);
return sResult;
    }
}
