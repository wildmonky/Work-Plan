import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_5b1ea6ae84bdacad80beb9b2db99b906
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String sResult=mapRow.get("ISCALL").toString();
sResult="1".equals(sResult)?"已电话通知":("0".equals(sResult)?"已电话通知":sResult);
return sResult;
    }
}
