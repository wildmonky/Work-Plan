import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_3f8a511b04b9922af7af0a7b11f34d9b
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return mapRow.get("DDZY").isEmpty()?"":"<span datavalue='"+mapRow.get("DDZY")+"' onclick='pw_notify_deadnumline_view(this);'>查看</span>";

    }
}
